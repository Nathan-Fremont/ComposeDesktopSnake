import androidx.compose.animation.core.keyframes
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private val screen = Screen()

//@OptIn(ExperimentalKeyInput::class)
fun main() = Window(
    title = "Snake",
    resizable = false,
    size = screen.getWindowSize(),
) {

    MaterialTheme {
        screen.getScreen()
    }
}