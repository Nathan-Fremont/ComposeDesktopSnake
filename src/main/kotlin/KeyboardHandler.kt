import androidx.compose.ui.input.key.KeyEvent

class KeyboardHandler {
    fun handleKeyEventForMovement(keyEvent: KeyEvent): Movement {
        println("handleKeyEventForMovement = $keyEvent")

        return when (keyEvent.nativeKeyEvent.keyChar) {
            'z' -> return Movement.UP
            'd' -> return Movement.RIGHT
            's' -> return Movement.DOWN
            'q' -> return Movement.LEFT
            else -> Movement.NONE
        }
    }

    fun handleKeyEventForActions(keyEvent: KeyEvent): Actions {
        println("handleKeyEventForActions = $keyEvent")

        return when (keyEvent.nativeKeyEvent.keyChar) {
            'p' -> return Actions.PAUSE
            else -> Actions.NONE
        }
    }
}