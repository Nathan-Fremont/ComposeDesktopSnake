sealed class Movement {
    abstract fun illegalMovement(): Movement
    object UP : Movement() {
        override fun illegalMovement(): Movement = DOWN
    }

    object RIGHT : Movement() {
        override fun illegalMovement(): Movement = LEFT
    }
    object DOWN : Movement() {
        override fun illegalMovement(): Movement = UP
    }
    object LEFT : Movement() {
        override fun illegalMovement(): Movement = RIGHT
    }
    object NONE: Movement() {
        override fun illegalMovement(): Movement = NONE
    }
}
