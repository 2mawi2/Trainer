package com.mawistudios.app

import android.util.Log

fun log(message: String) = Log.i("Trainer", message)

interface ILogger {
    fun log(message: String)
}

class Logger : ILogger {
    override fun log(message: String) {
        Log.i("Trainer", message)
    }
}
