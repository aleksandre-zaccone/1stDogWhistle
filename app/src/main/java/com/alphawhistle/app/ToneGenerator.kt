package com.alphawhistle.app

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

class ToneGenerator(context: Context) {

    companion object {
        private const val SAMPLE_RATE = 44100
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioTrack: AudioTrack? = null
    private var generatorThread: Thread? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    @Volatile private var active = false
    @Volatile var frequency: Double = 18000.0

    fun start() {
        if (active) return
        requestAudioFocus()

        val minBytes = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        // 4× min for smooth streaming; divide by 2 for ShortArray (2 bytes per short)
        val bufferShorts = (minBytes * 4) / 2

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferShorts * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        active = true

        generatorThread = Thread {
            val buffer = ShortArray(bufferShorts)
            var phase = 0.0
            while (active) {
                val freq = frequency
                for (i in buffer.indices) {
                    buffer[i] = (Short.MAX_VALUE * sin(2.0 * PI * phase)).toInt().toShort()
                    phase += freq / SAMPLE_RATE
                    if (phase >= 1.0) phase -= 1.0
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }.also { it.start() }
    }

    fun stop() {
        if (!active) return
        active = false
        generatorThread?.join(500)
        generatorThread = null
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        abandonAudioFocus()
    }

    private fun requestAudioFocus() {
        val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .build()
        audioFocusRequest = req
        audioManager.requestAudioFocus(req)
    }

    private fun abandonAudioFocus() {
        audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        audioFocusRequest = null
    }
}
