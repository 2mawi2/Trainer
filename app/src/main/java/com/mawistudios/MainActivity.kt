package com.mawistudios


import android.app.ListActivity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import com.mawistudios.app.log
import com.mawistudios.app.resetState
import com.mawistudios.data.hardware.ITrainingSessionObserver
import com.mawistudios.data.hardware.SensorService
import com.mawistudios.data.hardware.TrainingSessionObservable
import com.mawistudios.data.local.ISensorRepo
import com.mawistudios.app.model.Sensor
import com.mawistudios.features.workout.WorkoutActivity
import com.mawistudios.trainer.R
import com.mawistudios.trainer.R.layout
import org.koin.android.ext.android.inject
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : ListActivity() {
    private val sensorRepo: ISensorRepo by inject()

    lateinit var adapter: SensorAdapter

    var discoveredSensors = ArrayList<Sensor>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin()
        initPersistence()
        requestLocationPermission()

        setupUIComponents()

        log("Application started")
    }

    override fun onRequestPermissionsResult(
        code: Int, perm: Array<String?>, grants: IntArray
    ) {
        super.onRequestPermissionsResult(code, perm, grants)
        EasyPermissions.onRequestPermissionsResult(code, perm, grants, this)
    }

    private fun setupUIComponents() {
        setContentView(layout.activity_main)

        discoveredSensors.addAll(sensorRepo.all().map { it.resetState() })

        this.adapter =
            SensorAdapter(this, discoveredSensors)
        listAdapter = this.adapter

        findViewById<Button>(R.id.discoverButton).setOnClickListener {
            sensorRepo.all().forEach { sensorService.connectDevice(it) }
            sensorService.startDiscovery()
        }

        findViewById<Button>(R.id.trainer_button).let {
            it.setOnClickListener {
                log("Starting trainer")
                sensorService.stopDiscovery()
                startActivity(Intent(this, WorkoutActivity::class.java))
            }
            it.isEnabled = false
        }
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

    private val trainingSessionObserver = object :
        ITrainingSessionObserver {
        override fun onTrainingDataChanged() {}

        override fun onDiscoveryStarted() {
            output("discovery started")
        }

        override fun onSensorConnectionStateChanged(sensor: Sensor) {
            sensorRepo.addOrUpdate(sensor)
            sensorRepo.all().forEach { updateDiscoveredSensors(it) }
            adapter.notifyDataSetChanged()
            updateTrainerButtonStatus()
        }

        private fun updateDiscoveredSensors(sensor: Sensor) {
            if (discoveredSensors.any { it.name == sensor.name }) {
                discoveredSensors.firstOrNull { it.name == sensor.name }?.state = sensor.state
            } else {
                discoveredSensors.add(sensor)
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


