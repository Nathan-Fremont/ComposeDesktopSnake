import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme

private val game = Game()

fun main() = Window(
    title = "Snake",
    resizable = false,
    size = game.getWindowSize(),
) {

    MaterialTheme {
        game.getScreen()
    }
}