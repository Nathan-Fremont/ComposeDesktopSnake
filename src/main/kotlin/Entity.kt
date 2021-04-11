sealed class Entity(
    open val positionX: Int,
    open val positionY: Int
) {
    data class Snake(override val positionX: Int, override val positionY: Int): Entity(positionX, positionY)
    data class Apple(override val positionX: Int, override val positionY: Int): Entity(positionX, positionY)
}
