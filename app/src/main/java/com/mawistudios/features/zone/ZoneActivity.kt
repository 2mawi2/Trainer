package com.mawistudios.features.zone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.mawistudios.MainActivity
import com.mawistudios.app.format
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.activity_zone.*
import org.koin.android.ext.android.inject

class ZoneActivity : AppCompatActivity() {
    private val viewModel: ZoneViewModel by inject()
    private lateinit var zoneAdapter: ZoneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIComponents()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateLiveData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun setupUIComponents() {
        setContentView(R.layout.activity_zone)

        viewModel.ftpLiveData.observe(this, Observer { ftp ->
            findViewById<TextInputEditText>(R.id.ftp_edit_text).setText(ftp.format(0))
        })

        findViewById<TextInputEditText>(R.id.ftp_edit_text).addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text.toString().toDoubleOrNull()?.let {
                    viewModel.updateFtp(it)
                }
            }
        })

        viewModel.zonesLiveData.observe(this, Observer { zones ->
            zoneAdapter.submitList(zones)
        })

        if (!::zoneAdapter.isInitialized) {
            zoneAdapter = ZoneAdapter()
        }
        list_zones.adapter = zoneAdapter

        calculate_zones_btn.setOnClickListener {
            viewModel.recalculateZones()
        }
    }
}
