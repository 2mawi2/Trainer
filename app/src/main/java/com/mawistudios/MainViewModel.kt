package com.mawistudios

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.data.local.Sensor

class MainViewModel : ViewModel() {
    var discoveredSensors = MutableLiveData<Sensor>()
    var isDiscovering = MutableLiveData<Boolean>()



}