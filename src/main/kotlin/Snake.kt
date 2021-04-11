import kotlin.IllegalStateException

class Snake {
    lateinit var head: Entity.Snake
        private set
    private val parts = mutableListOf<Entity.Snake>()

    fun start(headX: Int, headY: Int) {
        head = Entity.Snake(headX, headY)
        parts.clear()
        addPart()
        addPart()
    }

    fun addPart() {
        parts.add(
            Entity.Snake(
                head.positionX,
                head.positionY
            )
        )
    }

    fun moveSnake(movement: Movement) {
        moveParts()
        moveHead(movement)
    }

    fun isOnPixel(x: Int, y: Int): Boolean =
        (parts + head).any { part ->
            part.positionX == x
                    && part.positionY == y
        }

    private fun moveParts() {
        for (index in parts.size - 1 downTo 1) {
            parts[index] = Entity.Snake(
                positionX = parts[index - 1].positionX,
                positionY = parts[index - 1].positionY
            )
        }
        parts[0] = Entity.Snake(
            positionX = head.positionX,
            positionY = head.positionY
        )
    }

    private fun moveHead(movement: Movement) {
        var newPositionX = head.positionX
        var newPositionY = head.positionY
        when (movement) {
            Movement.UP -> {
                newPositionY -= 1
            }
            Movement.RIGHT -> {
                newPositionX += 1
            }
            Movement.DOWN -> {
                newPositionY += 1
            }
            Movement.LEFT -> {
                newPositionX -= 1
            }
            else -> throw IllegalStateException("""
                moveHead => Movement should be one of these: 
                    - UP,
                    - RIGHT,
                    - DOWN,
                    - LEFT
            """.trimIndent()
            )
        }
        head = Entity.Snake(
            positionX = newPositionX,
            positionY = newPositionY
        )
    }
}