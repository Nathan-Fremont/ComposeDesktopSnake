sealed class Entity {
    data class Snake(val position: Position): Entity()
    data class Apple(val position: Position): Entity()
    object None: Entity()
}
