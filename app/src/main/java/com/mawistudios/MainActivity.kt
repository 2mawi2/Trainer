package com.mawistudios


import android.Manifest
import android.app.ListActivity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mawistudios.app.appModule
import com.mawistudios.app.log
import com.mawistudios.data.local.ObjectBox
import com.mawistudios.data.local.Sensor
import com.mawistudios.trainer.R
import com.mawistudios.trainer.R.layout
import org.koin.core.context.startKoin
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


interface ITrainingSessionObserver {
    fun onTrainingDataChanged()
    fun onDiscoveryStarted()
    fun onSensorConnectionStateChanged(
        deviceName: String,
        state: String
    )
}

class SensorAdapter(
    private val context: Context,
    private val dataSource: ArrayList<Sensor>
) : BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(layout.list_item_sensor, parent, false)

        val nameTextView = rowView.findViewById<TextView>(R.id.sensor_name)
        val statusTextView = rowView.findViewById<TextView>(R.id.sensor_status)
        val backgroundImage = rowView.findViewById<ImageView>(R.id.list_item_sensor_background)

        val sensor = getItem(position) as Sensor

        nameTextView.text = sensor.name
        statusTextView.text = sensor.state

        backgroundImage.setBackgroundColor(
            if (isSensorConnected(sensor)) context.getColor(R.color.colorAccent) else Color.WHITE
        )

        return rowView
    }

    private fun isSensorConnected(sensor: Sensor) = sensor.state.equals("connected", true)

    override fun getItem(position: Int): Any = dataSource[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getCount(): Int = dataSource.size
}

class MainActivity : ListActivity() {
    lateinit var adapter: SensorAdapter
    var discoveredSensors = ArrayList<Sensor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            printLogger()
            modules(appModule)
        }
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
        setContentView(layout.activity_main)

        this.adapter = SensorAdapter(this, discoveredSensors)
        listAdapter = this.adapter

        findViewById<Button>(R.id.discoverButton).setOnClickListener {
            log("Starting discovery")
            sensorService.startDiscovery()
        }

        findViewById<Button>(R.id.trainer_button).let {
            it.setOnClickListener {
                log("Starting trainer")
                startActivity(Intent(this, TrainerActivity::class.java))
            }
            it.isEnabled = false
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
        findViewById<TextView>(R.id.info).text = text
    }

}
