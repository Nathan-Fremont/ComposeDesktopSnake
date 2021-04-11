class Snake {
    lateinit var head: Entity.Snake
        private set
    private val parts = mutableListOf<Entity.Snake>()

    fun start(headPosition: Position) {
        head = Entity.Snake(headPosition)
        parts.clear()
        addPart()
        addPart()
    }

    fun addPart() {
        parts.add(
            Entity.Snake(
                Position(
                    -20,
                    -20
                )
            )
        )
    }

    fun moveSnake(movement: Movement) {
        moveParts()
        moveHead(movement)
    }

    fun isOnPixel(x: Int, y: Int) = isOnPixel(Position(x, y))

    fun isOnPixel(pixel: Position): Boolean =
        (parts + head).any { part ->
            part.position.x == pixel.x
                    && part.position.y == pixel.y
        }

    fun nextHeadPosition(movement: Movement): Position {
        var newX = head.position.x
        var newY = head.position.y
        when (movement) {
            Movement.UP -> {
                newY -= 1
            }
            Movement.RIGHT -> {
                newX += 1
            }
            Movement.DOWN -> {
                newY += 1
            }
            Movement.LEFT -> {
                newX -= 1
            }
            else -> throw IllegalStateException(
                """
                moveHead => Movement should be one of these: 
                    - UP,
                    - RIGHT,
                    - DOWN,
                    - LEFT
            """.trimIndent()
            )
        }
        return Position(newX, newY)
    }

    fun headCollidesWithParts(nextPosition: Position): Boolean {
        return parts.any { part ->
            part.position == nextPosition
        }
    }

    private fun moveParts() {
        for (index in parts.size - 1 downTo 1) {
            parts[index] = Entity.Snake(
                Position(
                    x = parts[index - 1].position.x,
                    y = parts[index - 1].position.y
                )
            )
        }
        parts[0] = Entity.Snake(
            Position(
                x = head.position.x,
                y = head.position.y
            )
        )
    }

    private fun moveHead(movement: Movement) {
        val position = nextHeadPosition(movement)
        head = Entity.Snake(
            Position(
                x = position.x,
                y = position.y
            )
        )
    }
}