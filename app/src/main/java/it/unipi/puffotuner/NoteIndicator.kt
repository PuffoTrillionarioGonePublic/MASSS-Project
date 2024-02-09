package it.unipi.puffotuner

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoteIndicator(note: String, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center, modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        var fontSize = with(LocalDensity.current) { maxWidth.toSp() / 10 }
        fontSize = if (fontSize < 24.sp) 24.sp else fontSize

        Text(
            text = note, style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = fontSize, // Ensure a minimum size
                fontWeight = FontWeight.Bold
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewNoteIndicator() {
    NoteIndicator(note = "A")
}
