package com.mawistudios


import android.Manifest
import android.app.ListActivity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.mawistudios.app.GlobalState
import com.mawistudios.app.appModule
import com.mawistudios.app.log
import com.mawistudios.data.local.ObjectBox
import com.mawistudios.data.local.Sensor
import com.mawistudios.trainer.R
import com.mawistudios.trainer.R.layout
import org.koin.core.context.startKoin
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : ListActivity() {
    lateinit var adapter: SensorAdapter
    var discoveredSensors = ArrayList<Sensor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin()
        initPersistence()
        requestLocationPermission()

        setupUIComponents()

        log("Application started")
    }

    private fun initPersistence() {
        if (!GlobalState.isObjectBoxInitialized) {
            ObjectBox.init(this)
            GlobalState.isObjectBoxInitialized = true
        }
    }

    private fun initKoin() {
        if (!GlobalState.isKoinInitialized) {
            startKoin {
                printLogger()
                modules(appModule)
            }
            GlobalState.isKoinInitialized = true
        }
    }

    @AfterPermissionGranted(1)
    fun requestLocationPermission() {
        if (!GlobalState.isLocationPermissionRequested) {
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
            GlobalState.isLocationPermissionRequested = true
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
        setContentView(layout.activity_main)

        this.adapter = SensorAdapter(this, discoveredSensors)
        listAdapter = this.adapter

        findViewById<Button>(R.id.discoverButton).setOnClickListener {
            startDiscovery()
        }

        findViewById<Button>(R.id.trainer_button).let {
            it.setOnClickListener {
                log("Starting trainer")
                startActivity(Intent(this, TrainerActivity::class.java))
            }
            it.isEnabled = false
        }
    }

    private fun startDiscovery() {
        log("Starting discovery")
        sensorService.startDiscovery()
    }

    private lateinit var sensorService: SensorService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            log("Service connected")
            val binder = service as SensorService.LocalBinder
            sensorService = binder.getService()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            log("Service disconnected")
        }
    }

    fun updateTrainerButtonStatus() {
        val isAtLeastOneSensorActive = discoveredSensors.any { it.isConnected() }
        findViewById<Button>(R.id.trainer_button).isEnabled = isAtLeastOneSensorActive
    }

    private val trainingSessionObserver = object : ITrainingSessionObserver {
        override fun onTrainingDataChanged() {}

        override fun onDiscoveryStarted() {
            output("discovery started")
        }

        override fun onSensorConnectionStateChanged(
            deviceName: String,
            state: String
        ) {
            addOrUpdateSensor(deviceName, state)
            adapter.notifyDataSetChanged()
            updateTrainerButtonStatus()
        }

        private fun addOrUpdateSensor(deviceName: String, state: String) {
            if (discoveredSensors.any { it.name == deviceName }) {
                discoveredSensors.firstOrNull { it.name == deviceName }?.state = state
            } else {
                discoveredSensors.add(
                    Sensor(name = deviceName, state = state)
                )
            }
        }
    }

    private var isServiceBound: Boolean = false
    override fun onStart() {
        super.onStart()
        TrainingSessionObservable.register(trainingSessionObserver)

        var intent = Intent(this, SensorService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
            isServiceBound = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
        }
    }

    override fun onStop() {
        super.onStop()
        TrainingSessionObservable.unRegister(trainingSessionObserver)
    }

    fun output(text: String) {
        findViewById<TextView>(R.id.info).text = text
    }
}
