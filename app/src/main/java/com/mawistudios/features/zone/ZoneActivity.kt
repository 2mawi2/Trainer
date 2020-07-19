package com.mawistudios.features.zone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mawistudios.MainActivity
import com.mawistudios.app.toast
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

        viewModel.zones.observe(this, Observer { zones ->
            zoneAdapter.submitList(zones)
        })

        if (!::zoneAdapter.isInitialized) {
            zoneAdapter = ZoneAdapter(
                onClickRemove = {
                    viewModel.removeZone(it)
                    toast("Zone deleted!")
                },
                onClickModify = { zone ->
                    TODO()
                }
            )
        }
        list_zones.adapter = zoneAdapter

        add_zone_btn.setOnClickListener {
            viewModel.addZonePlaceholder()
        }
    }
}
