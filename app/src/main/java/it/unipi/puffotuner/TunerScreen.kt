package it.unipi.puffotuner

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unipi.puffotuner.audioprocessing.AudioController
import it.unipi.puffotuner.audioprocessing.frequencyToNote
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tanh


fun normalizeAndClampFrequency(frequency: Float, min: Float, max: Float): Float {
    val mid = (min + max) / 2
    val range = (max - min) / 2
    return range * tanh((frequency - mid) / range) + range + 1
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TunerScreen(
    navController: NavController? = null,
    initialReferencePitch: Float = 440f,
    lowerPitch: Float = 430f,
    upperPitch: Float = 450f,
    title: String = "PuffoTuner",
) {
    var referencePitch by remember { mutableStateOf(initialReferencePitch) }
    var isTuning by remember { mutableStateOf(false) }
    var pitch by remember { mutableStateOf(0f) }
    Scaffold(topBar = {
        TunerAppBar(title = title, modifier = Modifier.fillMaxWidth(), navController = navController)
    }) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Slider(
                value = referencePitch,
                onValueChange = { referencePitch = it.roundToInt().toFloat() },
                valueRange = lowerPitch..upperPitch,
                steps = 20,
            )
            Text(text = "$referencePitch Hz", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))
            TuningMeter(
                centsOff = if (isTuning) pitch - referencePitch else 0f,
                needleColor = Color.Red,
                arcColors = listOf(Color.Red, Color.Green, Color.Red),
                scaleMarkingColors = listOf(Color.DarkGray, Color.LightGray),
                scaleMarkings = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            )
            val animatableFrequency = remember { Animatable(pitch) }
            val phase = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                phase.animateTo(
                    targetValue = (phase.value + 2 * PI).toFloat(), // Increment phase to move the wave
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )

            }

            LaunchedEffect(pitch) {
                animatableFrequency.animateTo(
                    targetValue = pitch, animationSpec = tween(durationMillis = 1000)
                )
            }
            if (isTuning) {
                val normalizedFrequency =
                    normalizeAndClampFrequency(pitch, lowerPitch, upperPitch)
                FrequencyWave(frequency = normalizedFrequency, phase = phase.value)
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
                            delay(500)
                            dots = dots % 3 + 1
                        }
                    }
                    val text = "Tuning".plus(".".repeat(dots)).plus(" ".repeat(3 - dots))
                    Text(text = text, style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text(text = "Ready")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            val note = frequencyToNote(pitch.toDouble())
            NoteIndicator(note = if (isTuning || pitch == 0f) note.prettyPrintItalian() else "-")
            val context = LocalContext.current
            Button(onClick = {
                isTuning = !isTuning
                if (isTuning) {
                    AudioController.startRecording(context = context,
                        onPitchDetected = { pitch = it.let { if (it.isNaN()) 0f else it } })
                }

            }) {
                Text(text = if (isTuning) "Stop" else "Start")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun FrequencyWave(
    frequency: Float, phase: Float, waveColor: Color = Color.Blue, strokeWidth: Float = 4f
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(200.dp)
    ) {
        val waveHeight = size.height / 2
        val waveWidth = size.width

        val centerX = waveWidth / 2
        var i = 0
        while (i < waveWidth.toInt() * 6) {
            val x = i.toFloat() / 6
            val angle = (2 * Math.PI * frequency * (x / waveWidth) + phase).toFloat()
            val tmp = (-(x - centerX).pow(2) / waveWidth.pow(2) + 1)
            val amplitudeModifier = tmp * tmp
            val y = waveHeight - (waveHeight * sin(angle) * amplitudeModifier)
            drawCircle(
                color = waveColor, center = Offset(x, y), radius = 2f, style = Stroke(strokeWidth)
            )
            i += 1
        }

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TunerScreen(
        initialReferencePitch = 440f,
        lowerPitch = 430f,
        upperPitch = 450f,
        title = "PuffoTuner"
    )
}