package com.example.greetingcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greetingcard.ui.theme.GreetingCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreetingCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    border = BorderStroke(1.dp, Color.Black),
                ) {
                    TunerApp()
                }
            }
        }
    }
}


@Composable
fun TopAppBarWithIcons() {
    // Define the surface color and elevation for your top app bar here
    val surfaceColor = Color(0xFF1E1E1E) // example color
    val elevation = 4.dp // example elevation

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // Standard height for top app bar
        color = surfaceColor,
        shadowElevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                modifier = Modifier.clickable { /* Handle menu click */ }
            )
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
fun TuningMeter(centsOff: Float) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWeight = 8.dp.toPx()
            val needleWidth = 4.dp.toPx()
            val needleHeight = size.height * 0.6f

            // Calculate the radius of the arc
            val radius = size.width.coerceAtMost(size.height)  - strokeWeight

            // Draw the semicircular arc for the meter
            drawArc(
                color = Color.Gray,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(
                    x = size.width / 2 - radius,
                    y = size.height - (radius)
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWeight)
            )

            // Draw the needle
            // save() // Save the current state of the canvas
            rotate(
                degrees = 120f * centsOff / 50f, // Assuming the full scale is 100 cents
                pivot = Offset(size.width / 2f, size.height),
                block = {
                    drawLine(
                        color = Color.Red,
                        start = Offset(x = size.width / 2f, y = size.height),
                        end = Offset(x = size.width / 2f, y = size.height - needleHeight),
                        strokeWidth = needleWidth
                    )
                }
            )
            drawLine(
                color = Color.Red,
                start = Offset(x = size.width / 2f, y = size.height),
                end = Offset(x = size.width / 2f, y = size.height - needleHeight),
                strokeWidth = needleWidth
            )
            //restore() // Restore the previous state of the canvas

            // Draw the center line
            drawLine(
                color = Color.Green,
                start = Offset(x = size.width / 2f, y = size.height),
                end = Offset(x = size.width / 2f, y = size.height - needleHeight),
                strokeWidth = needleWidth / 2
            )
        }

        // Text indicator for cents off
        Text(
            text = "${centsOff.toInt()}¢",
            modifier = Modifier.align(Alignment.TopCenter),
            color = Color.White
        )
    }
}


@Composable
fun TunerReadout(
    detectedFrequency: String = "A4: 440Hz", // Default values for preview
    centsOff: Int = 30
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frequency Display
        Text(
            text = detectedFrequency,
            // Add your text style here
        )

        // Cent off Display
        Row(verticalAlignment = Alignment.CenterVertically) {
            // This would be a custom composable if you want to make it look like the one in the image
            // For simplicity, we're using Text composables here
            Text(
                text = if (centsOff > 0) "+${centsOff}c" else "${centsOff}c",
                // Add your text style here
            )

            // You could add a visual indicator (e.g., arrow) here to represent the cents off
        }

        // The tuning indicator bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            // The indicator position should be calculated based on the cents off value
            // For now, we're placing it statically in the center
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(if (centsOff == 0) Color.Green else Color.Red),
                // contentAlignment = Alignment.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TunerReadoutPreview() {
    TunerReadout()
}


@Composable
fun TunerApp() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Top bar with settings and other icons
        //TopAppBarWithIcons()

        // Tuner readout section
        TunerReadout()

        // Large letter indicating the note
        NoteIndicator(note = "A")

        // Tuning meter display
        TuningMeter(centsOff = -3f)

        // Frequency graph at the bottom
        FrequencyGraph()
    }
}


@Composable
fun FrequencyGraph() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Mock data for the graph
            val points = listOf(
                Offset(0f, height / 2),
                // ... Add more points to create a mock waveform
                Offset(width, height / 2)
            )

            val path = Path().apply {
                // Start at the first point
                moveTo(points.first().x, points.first().y)

                // Create a line for each point
                for (point in points) {
                    lineTo(point.x, point.y)
                }
            }

            // Draw the graph within a clipping rectangle
            /* clipRect(Rect(0f, 0f, width, height)) {
                 drawPath(
                     path = path,
                     color = Color.White
                 )
             }*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FrequencyGraphPreview() {
    FrequencyGraph()
}


@Composable
fun NoteIndicator(note: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = note,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 100.sp, // You can adjust the size to match your design
                fontWeight = FontWeight.Bold
            ),
            color = Color.White // Assuming a dark theme, but you can customize it
        )
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
    TunerApp()
}
