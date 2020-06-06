package com.mawistudios

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mawistudios.data.local.Sensor
import com.mawistudios.trainer.R

class SensorAdapter(
    private val context: Context,
    private val dataSource: ArrayList<Sensor>
) : BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_sensor, parent, false)

        val nameTextView = rowView.findViewById<TextView>(
            R.id.sensor_name
        )
        val statusTextView = rowView.findViewById<TextView>(
            R.id.sensor_status
        )
        val backgroundImage = rowView.findViewById<ImageView>(
            R.id.list_item_sensor_background
        )

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