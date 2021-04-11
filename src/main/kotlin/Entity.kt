sealed class Entity {
    data class Snake(val positionX: Int, val positionY: Int): Entity()
    data class Apple(val positionX: Int, val positionY: Int): Entity()
    object None: Entity()
}
