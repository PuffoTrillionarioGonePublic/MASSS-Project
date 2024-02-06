package com.example.greetingcard


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.SweepGradient
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.greetingcard.ui.theme.GreetingCardTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreetingCardTheme {

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val drawerOpen by viewModel.drawerShouldBeOpened
                    .collectAsStateWithLifecycle()

                if (drawerOpen) {
                    // Open drawer and reset state in VM.
                    LaunchedEffect(Unit) {
                        // wrap in try-finally to handle interruption whiles opening drawer
                        try {
                            drawerState.open()
                        } finally {
                            viewModel.resetOpenDrawerAction()
                        }
                    }
                }

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
    val surfaceColor = Color(0xFF2D2F36) // Updated to a dark gray
    val elevation = 4.dp
    val textColor = Color(0xFFEDEDED) // Light gray for contrast

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
            Text(text = "Tuner", color = textColor, style = typography.titleMedium)
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
fun EnhancedTuningMeter(centsOff: Float, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWeight = 8.dp.toPx()
            val radius = size.width.coerceAtMost(size.height) * 0.9f - strokeWeight
            val center = Offset(x = size.width / 2, y = size.height)
            val needleHeight = size.height * 0.7f

            drawTuningArc(center, radius, strokeWeight)
            drawScaleMarkings(center, radius)
            drawNeedle(center, needleHeight, centsOff)
            drawCenterLine(center, needleHeight)
        }
    }
}

private fun DrawScope.drawTuningArc(center: Offset, radius: Float, strokeWeight: Float) {
    drawArc(
        brush = Brush.linearGradient(
            colors = listOf(Color.Red, Color.Green, Color.Red),
            start = Offset(x = center.x - radius, y = center.y),
            end = Offset(x = center.x + radius, y = center.y),
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(x = center.x - radius, y = center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWeight)
    )
}

private fun DrawScope.drawScaleMarkings(center: Offset, radius: Float) {
    val scaleMarkings = 10
    for (i in -scaleMarkings..scaleMarkings) {
        val angle = 180f / (scaleMarkings * 2) * i
        val markingRadius = calculateMarkingRadius(i, radius)
        drawScaleLine(center, radius, markingRadius, angle, i)
        drawScaleText(center, radius, angle, i)
    }
}

fun DrawScope.drawScaleLine(
    center: Offset,
    radius: Float,
    markingRadius: Float,
    angle: Float,
    i: Int
) {
    val start = Offset(
        x = center.x + radius * sin(Math.toRadians(angle.toDouble() + 180.0)).toFloat(),
        y = center.y + radius * cos(Math.toRadians(angle.toDouble() + 180.0)).toFloat()
    )
    val end = Offset(
        x = center.x + markingRadius * sin(Math.toRadians(angle.toDouble() + 180.0)).toFloat(),
        y = center.y + markingRadius * cos(Math.toRadians(angle.toDouble() + 180.0)).toFloat()
    )
    drawLine(
        brush = Brush.linearGradient(colors = listOf(Color.DarkGray, Color.LightGray)),
        start = start,
        end = end,
        strokeWidth = if (i % 5 == 0) 4.dp.toPx() else 2.dp.toPx()
    )
}

fun DrawScope.drawScaleText(center: Offset, radius: Float, angle: Float, i: Int) {
    if (i % 5 == 0) {
        val textRadius = radius - 35.dp.toPx()
        val textOffset = Offset(
            x = center.x + textRadius * sin(Math.toRadians(angle.toDouble() + 180.0)).toFloat(),
            y = center.y + textRadius * cos(Math.toRadians(angle.toDouble() + 180.0)).toFloat()
        )
        drawContext.canvas.nativeCanvas.drawText(
            "${i * 5}",
            textOffset.x,
            textOffset.y,
            android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.Black.toArgb()
                textSize = 40f
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}

private fun DrawScope.calculateMarkingRadius(i: Int, radius: Float): Float {
    return if (i % 5 == 0) radius - 20.dp.toPx() else radius - 10.dp.toPx()
}

private fun DrawScope.drawNeedle(center: Offset, needleHeight: Float, centsOff: Float) {
    val needleWidth = 4.dp.toPx()
    rotate(degrees = 180f * centsOff / 50f, pivot = center, block = {
        drawLine(
            color = Color.Red,
            start = center,
            end = Offset(x = center.x, y = center.y - needleHeight),
            strokeWidth = needleWidth
        )
    })
}

private fun DrawScope.drawCenterLine(center: Offset, needleHeight: Float) {
    drawLine(
        color = Color.Green,
        start = center,
        end = Offset(x = center.x, y = center.y - needleHeight),
        strokeWidth = 2.dp.toPx()
    )
}




@Composable
@Preview(showBackground = true)
fun EnhancedTuningMeterPreview() {
    EnhancedTuningMeter(centsOff = 0f)
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


class MainViewModel : ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }
}



@Composable
fun FrequencyWave(pitch: Float, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (1000 / pitch).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .height(200.dp) // You can adjust the size as needed
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



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TunerScreen() {
    var pitch by remember { mutableIntStateOf(440) }
    var isTuning by remember { mutableStateOf(false) }
    Scaffold(topBar = {
            TunerAppBar(title = "Tuner", onNavIconPressed = { })//activityViewModel.openDrawer() })
        }){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
            EnhancedTuningMeter(centsOff = 0f)
            FrequencyWave(pitch = 5.0f)

            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
            ) {
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

@Preview(showBackground = true)
@Composable
fun FrequencyWavePreview() {
    FrequencyWave(pitch = 5.0f)
}
