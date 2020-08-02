package com.mawistudios.features.workout.detail.interval

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.hoursToMillis
import com.mawistudios.app.minutesToMillis
import com.mawistudios.app.model.Interval
import com.mawistudios.app.secondsToMillis
import com.mawistudios.features.trainer.IIntervalRepo

class IntervalDetailViewModel(
    private val intervalRepo: IIntervalRepo
) : ViewModel() {
    val interval: MutableLiveData<Interval> by lazy { MutableLiveData<Interval>() }
    private var intervalId: Long? = null
    var workoutId: Long? = null

    fun setInterval(intervalId: Long) {
        this.intervalId = intervalId
        updateLiveData()
    }

    fun updateLiveData() {
        interval.value = intervalId?.let { intervalRepo.get(it) }
    }

    fun saveInterval() {
        interval.value?.let { intervalRepo.save(it) }
    }

    fun setIntervalName(input: String) {
        interval.value?.let {
            it.name = input
            interval.value = it
        }
    }

    fun setIntervalDuration(hours: Int, minutes: Int, seconds: Int) {
        interval.value?.let {
            it.hours = hours
            it.minutes = minutes
            it.seconds = seconds
            interval.value = it
        }
    }
}