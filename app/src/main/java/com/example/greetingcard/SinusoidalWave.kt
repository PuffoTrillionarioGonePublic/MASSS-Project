import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.Stroke
@Composable
fun SinusoidalWave(pitch: Float, modifier: Modifier = Modifier) {
    val wavePaint = Paint().apply {
        color = Color.Blue
        style = PaintingStyle.Stroke
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height / 2
        val amplitude = 100f  // Adjust the amplitude of the wave as needed
        val gradient = Brush.verticalGradient(
            colors = listOf(Color.Cyan, Color.Blue),
            startY = 0f,
            endY = size.height
        )

        for (x in 0..width.toInt()) {
            val angle = (x + (pitch - 440f)) * Math.PI / 180 * pitch / 440f
            val y = (Math.sin(angle) * amplitude).toFloat() + height

            drawCircle(gradient, radius = 10f, center = Offset(x.toFloat(), y))
        }
    }
}


