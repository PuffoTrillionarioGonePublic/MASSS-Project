package com.example.greetingcard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin
/*
@Composable
fun FrequencyWave(pitch: Float, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .height(200.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSineWave(pitch, phase)
        }
    }
}

fun DrawScope.drawSineWave(pitch: Float, phase: Float) {
    val amplitude = size.height / 4 // Adjust amplitude to fit the container
    val frequency = (2 * Math.PI / size.width) * pitch
    val path = Path().apply {
        moveTo(0f, size.height / 2)
        for (x in 0 until size.width.toInt()) {
            val y = amplitude * sin(frequency * x + phase) + size.height / 2
            lineTo(x.toFloat(), y.toFloat())
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Cyan, Color.Blue),
        startY = 0f,
        endY = size.height
    )

    drawPath(
        path = path,
        brush = gradient,
        style = Stroke(width = 4f, cap = StrokeCap.Round)
    )
}



@Preview(showBackground = true)
@Composable
fun FrequencyWavePreview() {
    FrequencyWave(pitch = 5.0f)
}*/


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
@Composable
fun FrequencyWave(frequency: Float, phase: Float) {
    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val waveHeight = size.height / 2
        val waveWidth = size.width

        for (x in 0 until waveWidth.toInt()) {
            val angle = (2 * Math.PI * frequency * (x / waveWidth) + phase).toFloat()
            val y = waveHeight - (waveHeight * kotlin.math.sin(angle))
            drawCircle(
                color = Color.Blue,
                center = Offset(x.toFloat(), y),
                radius = 2f,
                style = Stroke(1f)
            )
        }
    }
}*/