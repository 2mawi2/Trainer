package com.mawistudios


import android.Manifest
import android.app.ListActivity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.mawistudios.app.log
import com.mawistudios.data.local.ObjectBox
import com.mawistudios.trainer.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


object TrainingSessionObservable {
    private var observers = mutableListOf<ITrainingSessionObserver>()

    fun register(trainingSessionObserver: ITrainingSessionObserver) {
        observers.add(trainingSessionObserver)
    }

    fun unRegister(trainingSessionObserver: ITrainingSessionObserver) {
        observers.removeIf { it == trainingSessionObserver }
    }

    fun onTrainingDataChanged() = observers.forEach { it.onTrainingDataChanged() }
    fun onDiscoveryStarted() = observers.forEach { it.onDiscoveryStarted() }
    fun onSensorConnectionStateChanged(deviceName: String, state: String) {
        observers.forEach { it.onSensorConnectionStateChanged(deviceName, state) }
    }
}

interface ITrainingSessionObserver {
    fun onTrainingDataChanged()
    fun onDiscoveryStarted()
    fun onSensorConnectionStateChanged(deviceName: String, state: String)
}

class MainActivity : ListActivity() {
    lateinit var adapter: ArrayAdapter<String>
    var listItems = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermission()
        ObjectBox.init(this)
        setupUIComponents()
        log("Application started")
    }

    @AfterPermissionGranted(1)
    fun requestLocationPermission() {
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Please grant the location permission",
                1,
                *perms
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    private fun setupUIComponents() {
        setContentView(R.layout.activity_main)

        this.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listItems
        )

        listAdapter = this.adapter
        findViewById<Button>(R.id.discoverButton).setOnClickListener {
            log("Starting discovery")
            sensorService.startDiscovery()
        }
        findViewById<Button>(R.id.trainer_button).setOnClickListener {
            log("Starting trainer")
            startActivity(Intent(this, TrainerActivity::class.java))
        }
    }

    private lateinit var sensorService: SensorService
    private var isSensorServiceBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            log("Service connected")
            val binder = service as SensorService.LocalBinder
            sensorService = binder.getService()
            isSensorServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            log("Service disconnected")
            isSensorServiceBound = false
        }
    }

    private val trainingSessionObserver = object : ITrainingSessionObserver {
        override fun onTrainingDataChanged() {}

        override fun onDiscoveryStarted() {
            output("discovery started")
        }

        override fun onSensorConnectionStateChanged(deviceName: String, state: String) {
            if (listItems.any { it.contains(deviceName) }) {
                listItems.forEachIndexed { index, s ->
                    if (s.contains(deviceName)) {
                        listItems[index] = "$deviceName $state"
                    }
                }
            } else {
                listItems.add("$deviceName $state")
            }

            adapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        TrainingSessionObservable.register(trainingSessionObserver)

        Intent(this, SensorService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        TrainingSessionObservable.unRegister(trainingSessionObserver)
        isSensorServiceBound = false
    }

    fun output(text: String) {
        listItems.add(text)
        adapter.notifyDataSetChanged()
    }

}
