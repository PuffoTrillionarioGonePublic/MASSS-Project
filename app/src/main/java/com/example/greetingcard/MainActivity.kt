package com.example.greetingcard


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greetingcard.ui.theme.GreetingCardTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreetingCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    border = BorderStroke(1.dp, Color.Black),
                ) {
                    TunerScreen()
                }
            }
        }
    }
}


@Composable
fun TopAppBarWithIcons() {
    val surfaceColor = Color(0xFF1E1E1E)
    val elevation = 4.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = surfaceColor, shadowElevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Menu",
                modifier = Modifier.clickable { /* Handle menu click */ })
            Text(text = "Tuner", color = Color.White)
            IconButton(onClick = { /* Handle settings click */ }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopAppBarWithIconsPreview() {
    TopAppBarWithIcons()
}


@Composable
fun TuningMeter(centsOff: Float, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWeight = 8.dp.toPx()
            val needleWidth = 4.dp.toPx()
            val needleHeight = size.height * 0.7f
            val radius = size.width.coerceAtMost(size.height) - strokeWeight
            val center = Offset(x = size.width / 2, y = size.height)

            drawArc(
                color = Color.Gray,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(x = center.x - radius, y = center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWeight)
            )

            val scaleMarkings = 10
            for (i in -scaleMarkings..scaleMarkings) {
                val angle = 180f / (scaleMarkings * 2) * i
                val innerRadius = if (i % 5 == 0) radius - 20.dp.toPx() else radius - 10.dp.toPx()
                val start = Offset(
                    x = center.x + radius * sin(Math.toRadians(angle.toDouble() + 180.0)).toFloat(),
                    y = center.y + radius * cos(Math.toRadians(angle.toDouble() + 180.0)).toFloat()
                )
                val end = Offset(
                    x = center.x + innerRadius * sin(Math.toRadians(angle.toDouble() + 180.0)).toFloat(),
                    y = center.y + innerRadius * cos(Math.toRadians(angle.toDouble() + 180.0)).toFloat()
                )
                drawLine(
                    color = Color.Black,
                    start = start,
                    end = end,
                    strokeWidth = if (i % 5 == 0) 4.dp.toPx() else 2.dp.toPx()
                )

                if (i % 5 == 0) {
                    val textRadius = radius - 30.dp.toPx()
                    val textOffset = Offset(
                        x = center.x + textRadius * sin(Math.toRadians(angle.toDouble() + 180.0)).toFloat(),
                        y = center.y + textRadius * cos(Math.toRadians(angle.toDouble() + 180.0)).toFloat()
                    )
                    drawContext.canvas.nativeCanvas.drawText("${i * 5}",

                        textOffset.x,
                        textOffset.y,
                        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.Black.toArgb()
                            textSize = 40f
                            textAlign = android.graphics.Paint.Align.CENTER
                        })

                }


            }

            rotate(degrees = 180f * centsOff / 50f,
                pivot = center, block = {
                    drawLine(
                        color = Color.Red,
                        start = center,
                        end = Offset(x = center.x, y = center.y - needleHeight),
                        strokeWidth = needleWidth
                    )
                })

            drawLine(
                color = Color.Green,
                start = center,
                end = Offset(x = center.x, y = center.y - needleHeight),
                strokeWidth = needleWidth / 2
            )
        }

    }
}


@Composable
fun FrequencyBars(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            val width = size.width
            val barWidth = width / 60
            val barHeights = List(60) { kotlin.random.Random.nextFloat() }
            val height = size.height
            barHeights.forEachIndexed { index, heightFraction ->
                val barHeight = height * heightFraction
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(x = barWidth * index, y = this.size.height - barHeight),
                    size = Size(width = barWidth, height = barHeight)
                )
            }
        }
    }
}


@Composable
fun NoteIndicator(note: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = note, style = typography.titleLarge.copy(
                fontSize = 100.sp, // You can adjust the size to match your design
                fontWeight = FontWeight.Bold
            ), color = Color.Black // Assuming a dark theme, but you can customize it
        )
    }
}




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TunerScreen() {
    var pitch by remember { mutableIntStateOf(440) }
    var isTuning by remember { mutableStateOf(false) }
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Tuner", style = typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "A4 Frequency Adjustment", fontSize = 16.sp)
            Slider(
                value = pitch.toFloat(),
                onValueChange = { pitch = it.toInt() },
                valueRange = 430f..450f,
                steps = 20
            )
            Text(text = "$pitch Hz", style = typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))
            TuningMeter(centsOff = 0f)

            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.size(90.dp)) {
                    // Draw your pitch representation here
                }
                if (isTuning) {
                    Text(text = "Tuning...", color = Color.Black)
                } else {
                    Text(text = "Ready", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            NoteIndicator(note = "A")
            Button(onClick = { isTuning = !isTuning }) {
                Text(text = if (isTuning) "Stop" else "Start")
            }
            Spacer(modifier = Modifier.height(20.dp))
            FrequencyBars()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteIndicator() {
    NoteIndicator(note = "A")
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TunerScreen()
}
