package com.example.greetingcard

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greetingcard.audioprocessing.AudioController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TunerScreen() {
    var referencePitch by remember { mutableStateOf(440f) }
    var isTuning by remember { mutableStateOf(false) }
    var pitch by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()


    Scaffold(topBar = {
        TunerAppBar(title = "PuffoTuner", onNavIconPressed = { })//activityViewModel.openDrawer() })
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Slider(
                value = referencePitch,
                onValueChange = { referencePitch = it },
                valueRange = 430f..450f,
                steps = 20
            )
            Text(text = "$referencePitch Hz", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))
            TuningMeter(centsOff = if (isTuning) pitch - referencePitch else 0f)

            //var frequency by remember { mutableStateOf(1f) }
            val animatableFrequency = remember { Animatable(pitch) }
            val phase = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                phase.animateTo(
                    targetValue = (phase.value + 2*PI).toFloat(), // Increment phase to move the wave
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )

            }

            LaunchedEffect(pitch) {
                animatableFrequency.animateTo(
                    targetValue = pitch,
                    animationSpec = tween(durationMillis = 1000) // Smooth transition over 1 second
                )
            }
            if (isTuning) {
                FrequencyWave(frequency = animatableFrequency.value, phase = phase.value)
            } else {
                FrequencyWave(frequency = 0f, phase = 0f)
            }

            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
            ) {
                if (isTuning) {
                    var dots by remember { mutableIntStateOf(1) }
                    LaunchedEffect(Unit) {
                        while (isTuning) {
                            dots = (dots + 1) % 4
                            delay(500)
                        }
                    }
                    Text(text = "Tuning".plus(".".repeat(dots)))
                } else {
                    Text(text = "Ready")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            NoteIndicator(note = "A")
            val context = LocalContext.current
            Button(onClick = {
                isTuning = !isTuning
                if (isTuning) {
                    AudioController.startRecording(context = context, onPitchDetected = { pitch = it })
                }

            }) {
                Text(text = if (isTuning) "Stop" else "Start")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun FrequencyWave(frequency: Float, phase: Float) {
    Canvas(modifier = Modifier.fillMaxWidth().padding(20.dp).height(200.dp)) {
        val waveHeight = size.height / 2
        val waveWidth = size.width

        for (x in 0 until waveWidth.toInt()) {
            val tmp = x.toFloat() / waveWidth.toInt().toFloat()
            val m: Float = (1 - (tmp - PI) * (tmp - PI) / PI * PI).toFloat()
            val angle = (2 * Math.PI * frequency * (x / waveWidth) + phase).toFloat()
            val y = waveHeight - (waveHeight * sin(angle)/m)
            drawCircle(
                color = Color.Blue,
                center = Offset(x.toFloat(), y),
                radius = 2f,
                style = Stroke(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TunerScreen()
}