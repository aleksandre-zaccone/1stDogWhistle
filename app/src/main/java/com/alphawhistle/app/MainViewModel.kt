package com.alphawhistle.app

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("whistle_prefs", Context.MODE_PRIVATE)
    private val toneGenerator = ToneGenerator(application)

    var frequency by mutableFloatStateOf(prefs.getFloat("frequency", 18000f))
        private set

    var isPlaying by mutableStateOf(false)
        private set

    fun startPlaying() {
        toneGenerator.frequency = frequency.toDouble()
        toneGenerator.start()
        isPlaying = true
    }

    fun stopPlaying() {
        toneGenerator.stop()
        isPlaying = false
    }

    fun updateFrequency(hz: Float) {
        frequency = hz
        toneGenerator.frequency = hz.toDouble()
        prefs.edit().putFloat("frequency", hz).apply()
    }

    override fun onCleared() {
        super.onCleared()
        toneGenerator.stop()
    }
}
