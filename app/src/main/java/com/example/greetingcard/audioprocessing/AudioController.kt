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
import kotlin.math.ln
import kotlin.math.roundToInt


enum class Tone {
    C, D, E, F, G, A, B,
}

enum class Semitone {
    NATURAL, SHARP, FLAT;

    override fun toString(): String {
        return when (this) {
            NATURAL -> ""
            SHARP -> "♯"
            FLAT -> "♭"
        }
    }
}

data class Note(
    val tone: Tone,
    val semitone: Semitone,
    val octave: Int
) {
    fun prettyPrintItalian(): String {
        val toneName = when (tone) {
            Tone.C -> "Do"
            Tone.D -> "Re"
            Tone.E -> "Mi"
            Tone.F -> "Fa"
            Tone.G -> "Sol"
            Tone.A -> "La"
            Tone.B -> "Si"
        }
        return "$toneName$semitone"
    }
}

fun frequencyToNote(frequency: Double): Note {
    val semitoneFromA4 = 12 * ln(frequency / 440.0) / ln(2.0)
    val roundedSemitone = semitoneFromA4.roundToInt()
    var totalSemitonesFromC0 = roundedSemitone + 9 + (4 * 12) // C0 is 9 semitones below A4, plus 4 octaves
    val octave = totalSemitonesFromC0 / 12
    while (totalSemitonesFromC0 < 0) {
        totalSemitonesFromC0 += 12
    }
    val toneAndSemitone = when (totalSemitonesFromC0 % 12) {
        0 -> Pair(Tone.C, Semitone.NATURAL)
        1 -> Pair(Tone.C, Semitone.SHARP)
        2 -> Pair(Tone.D, Semitone.NATURAL)
        3 -> Pair(Tone.D, Semitone.SHARP)
        4 -> Pair(Tone.E, Semitone.NATURAL)
        5 -> Pair(Tone.F, Semitone.NATURAL)
        6 -> Pair(Tone.F, Semitone.SHARP)
        7 -> Pair(Tone.G, Semitone.NATURAL)
        8 -> Pair(Tone.G, Semitone.SHARP)
        9 -> Pair(Tone.A, Semitone.NATURAL)
        10 -> Pair(Tone.A, Semitone.SHARP)
        11 -> Pair(Tone.B, Semitone.NATURAL)
        else -> {
            throw IllegalArgumentException("Invalid note index")
        }
    }

    return Note(
        tone = toneAndSemitone.first,
        semitone = toneAndSemitone.second,
        octave = octave
    )
}




object AudioController {

    private val MY_PERMISSIONS_RECORD_AUDIO = 1


    fun startRecording(context: Context, onPitchDetected: (Float) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_RECORD_AUDIO
            )
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val sampleRate = 44100
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

            try {
                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize
                )
                audioRecord.startRecording()
                val buffer = ShortArray(minBufferSize)

                while (audioRecord.read(buffer, 0, minBufferSize) > 0) {
                    val floatBuffer = buffer.map { it.toFloat() }.toFloatArray()
                    val pitch = yinPitchDetection(floatBuffer, sampleRate)
                    onPitchDetected(pitch)
                }

            } catch (e: IllegalStateException) {
                Log.e("AudioRecord", "Recording failed: ${e.message}")
            } catch (e: IOException) {
                Log.e("AudioRecord", "IO Exception: ${e.message}")
            }
        }
    }


}
