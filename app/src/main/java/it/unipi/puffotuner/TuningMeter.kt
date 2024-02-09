package it.unipi.puffotuner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun TuningMeter(
    centsOff: Float,
    modifier: Modifier = Modifier,
    //meterHeight: Dp,
    //padding: Dp,
    needleColor: Color = Color.Red,
    arcColors: List<Color>,
    scaleMarkingColors: List<Color>,
    scaleMarkings: Int
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWeight = 8.dp.toPx()
            val radius = size.width.coerceAtMost(size.height) * 0.9f - strokeWeight
            val center = Offset(x = size.width / 2, y = size.height)
            val needleHeight = size.height * 0.7f

            drawTuningArc(center, radius, strokeWeight, arcColors)
            drawScaleMarkings(center, radius, scaleMarkings, scaleMarkingColors)
            drawNeedle(center, needleHeight, centsOff, needleColor)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Yellow, Color.Red),
                    center = center,
                    radius = strokeWeight / 2,
                ),
                radius = strokeWeight / 2,
                center = center,
            )
        }
    }
}

private fun DrawScope.drawTuningArc(center: Offset, radius: Float, strokeWeight: Float, colors: List<Color>) {
    drawArc(
        brush = Brush.linearGradient(
            colors = colors,
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



private fun DrawScope.calculateMarkingRadius(i: Int, radius: Float): Float {
    return if (i % 5 == 0) radius - 20.dp.toPx() else radius - 10.dp.toPx()
}

private fun DrawScope.drawScaleMarkings(center: Offset, radius: Float, scaleMarkings: Int, colors: List<Color>) {
    for (i in -scaleMarkings..scaleMarkings) {
        val angle = -180f / (scaleMarkings * 2) * i
        val markingRadius = calculateMarkingRadius(i, radius)
        drawScaleLine(center, radius, markingRadius, angle, i, colors)
        // drawScaleText can also be adjusted to accept custom parameters if needed
    }
}


fun DrawScope.drawScaleLine(
    center: Offset, radius: Float, markingRadius: Float, angle: Float, i: Int, colors: List<Color>
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
        brush = Brush.linearGradient(colors = colors),
        start = start,
        end = end,
        strokeWidth = if (i % 5 == 0) 4.dp.toPx() else 2.dp.toPx()
    )
}

private fun DrawScope.drawNeedle(center: Offset, needleHeight: Float, centsOff: Float, needleColor: Color) {
    val needleWidth = 4.dp.toPx()
    val needleHeadRadius = needleWidth / 2

    val color = needleColor
    val centsOff = when {
        centsOff < -50 -> -50f
        centsOff > 50 -> 50f
        else -> centsOff
    }

    rotate(degrees = 90f * centsOff / 50f, pivot = center, block = {
        drawLine(
            color = color,
            start = center,
            end = Offset(
                x = center.x,
                y = center.y - needleHeight + needleHeadRadius
            ),
            strokeWidth = needleWidth
        )
        drawCircle(
            color = color,
            radius = needleHeadRadius,
            center = Offset(x = center.x, y = center.y - needleHeight + needleHeadRadius),
        )
    })
}

@Composable
@Preview(showBackground = true)
fun EnhancedTuningMeterPreview() {
    TuningMeter(centsOff = 0f,
        needleColor = Color.Red,
        arcColors = listOf(Color.Red, Color.Green, Color.Red),
        scaleMarkingColors = listOf(Color.DarkGray, Color.LightGray),
        scaleMarkings = 10,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    )
}