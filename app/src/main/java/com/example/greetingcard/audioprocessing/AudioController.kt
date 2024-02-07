package com.example.greetingcard.audioprocessing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException


object AudioController {

    private val MY_PERMISSIONS_RECORD_AUDIO = 1

    fun startRecording(context: Context, onPitchDetected: (Float) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val sampleRate = 44100 // You can choose a different sample rate
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            }
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize
            )


            val buffer = ByteArray(minBufferSize)
            val outputStream = ByteArrayOutputStream()

            try {
                audioRecord.startRecording()

                val shortBuffer = ShortArray(minBufferSize)
                val doubleBuffer = DoubleArray(minBufferSize)
                var doubleBuffer2 = DoubleArray(minBufferSize)
                var shortBuffer2 = ShortArray(minBufferSize)
                while (audioRecord.read(shortBuffer, 0, minBufferSize) > 0) {
                    for (i in shortBuffer.indices) {
                        doubleBuffer[i] = shortBuffer[i].toDouble()
                    }
                    if (doubleBuffer2.size != minBufferSize) {
                        doubleBuffer2 = DoubleArray(minBufferSize)
                        shortBuffer2 = ShortArray(minBufferSize)
                    }
                    System.arraycopy(doubleBuffer, 0, doubleBuffer2, 0, minBufferSize)
                    System.arraycopy(shortBuffer, 0, shortBuffer2, 0, minBufferSize)
                   // for (i in doubleBuffer.indices) {
                    //    f(shortBuffer2[i], doubleBuffer2[i])
                    //}
                    //(shortBuffer2, doubleBuffer2)
                    //PitchDetector.detectPitch(shortBuffer2, doubleBuffer2, onPitchDetected)
                    val floatBuffer = doubleBuffer.map { it.toFloat() }.toFloatArray()
                    val pitch = yinPitchDetection(floatBuffer, sampleRate)
                    onPitchDetected(pitch)
                }

                audioRecord.stop()

                // At this point, outputStream.toByteArray() contains the recorded audio data in memory
                val audioData = outputStream.toByteArray()

            } catch (e: IOException) {
                Log.e("AudioRecord", "Recording failed")
            } finally {
                audioRecord.release()
                outputStream.close()
            }
        }

    }


}
