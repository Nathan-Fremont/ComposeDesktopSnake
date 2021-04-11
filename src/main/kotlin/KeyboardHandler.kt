import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type

class KeyboardHandler {
    fun handleKeyEventForMovement(keyEvent: KeyEvent): Movement {
        return when (keyEvent.nativeKeyEvent.keyChar) {
            'z' -> Movement.UP
            'd' -> Movement.RIGHT
            's' -> Movement.DOWN
            'q' -> Movement.LEFT
            else -> Movement.NONE
        }
    }

    fun handleKeyEventForActions(keyEvent: KeyEvent): Actions {
        if (keyEvent.type == KeyEventType.KeyUp) {
            return when (keyEvent.nativeKeyEvent.keyChar) {
                'p' -> Actions.PAUSE
                'r' -> Actions.RETRY
                else -> Actions.NONE
            }
        }
        return Actions.NONE
    }
}