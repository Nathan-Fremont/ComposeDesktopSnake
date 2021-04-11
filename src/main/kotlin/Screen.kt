import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.random.Random

class Screen {

    companion object {
        private const val SWITCH_ROWS = 20
        private const val SWITCH_COLUMNS = 20

        private val SWITCH_WIDTH = 34.dp
        private val SWITCH_HEIGHT = 20.dp
        private val SWITCH_PADDING = 10.dp
    }

    private val screenWidth: Float by lazy {
        return@lazy (SWITCH_WIDTH.value + SWITCH_PADDING.value) * SWITCH_COLUMNS
    }

    private val screenHeight: Float by lazy {
        (SWITCH_HEIGHT.value + SWITCH_PADDING.value) * SWITCH_ROWS
    }

    private val snake = mutableListOf<Entity.Snake>()
    private var apple: Entity.Apple? = null

    private var currentMovement: Movement = Movement.RIGHT
    private lateinit var gameLoop: Loop
    private lateinit var updatedFrame: MutableState<Int>
    private val keyboardHandler = KeyboardHandler()

    init {
        snake.add(Entity.Snake(SWITCH_ROWS / 2, SWITCH_COLUMNS / 2))
        snake.add(Entity.Snake(snake[0].positionX - 1, snake[0].positionY))
        snake.add(Entity.Snake(snake[1].positionX - 1, snake[1].positionY))
        generateApple()
    }



    fun getWindowSize() = IntSize(
        width = screenWidth.roundToInt(),
        height = screenHeight.roundToInt()
    )

    @Composable
    fun getScreen() {
        gameLoop = remember {
            Loop()
        }
        updatedFrame = remember {
            mutableStateOf(0)
        }

        println("GetScreen")

        getFrame()

        LaunchedEffect(Unit) {
            while (true) {
                withFrameMillis { frame ->
//                    println("Frame = $frame")
                    if (gameLoop.shouldUpdate()) {
                        updatedFrame.value += 1
                        update()
                        gameLoop.markAsUpdated()
                    }
                }
            }
        }
    }

    @Composable
    private fun getFrame() {
        parseScreen(object : PixelCallback {
            @Composable
            override fun drawForPixel(column: Int, row: Int) {
//                snake.forEach { part ->
                    getPixel(
                        isSnakeOnPixel(column, row)
                    )
//                }
            }
        })
    }

    private fun update() {
        println("update = ${updatedFrame.value}")
        val canMove = checkMove()
        if (canMove) {
            moveSnake()
            checkApple()
        } else {
            gameLoop.stopUpdating()
        }
    }

    private fun checkMove(): Boolean {
        var canMove = true
        when (currentMovement) {
            Movement.UP -> {
                if (snake[0].positionY == 0) {
                    canMove = false
                }
            }
            Movement.RIGHT -> {
                if (snake[0].positionX == SWITCH_COLUMNS) {
                    canMove = false
                }
            }
            Movement.DOWN -> {
                if (snake[0].positionY == SWITCH_ROWS) {
                    canMove = false
                }
            }
            Movement.LEFT -> {
                if (snake[0].positionX == 0) {
                    canMove = false
                }
            }
        }
        return canMove
    }

    private fun moveSnake() {
        for (index in snake.size - 1 downTo 1) {
            snake[index] = Entity.Snake(
                positionX = snake[index - 1].positionX,
                positionY = snake[index - 1].positionY
            )
        }
        var newPositionX = snake[0].positionX
        var newPositionY = snake[0].positionY
        when (currentMovement) {
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
        }
        snake[0] = Entity.Snake(
            positionX = newPositionX,
            positionY = newPositionY
        )
    }

    private fun checkApple() {

    }

    @Composable
    private fun parseScreen(pixelCallback: PixelCallback) {
        println("parseScreen")
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .focusModifier()
                .focusable(enabled = true)
                .focusRequester(FocusRequester.Default)
                .onKeyEvent { event ->
                    val newMovement = keyboardHandler.handleKeyEventForMovement(keyEvent = event)
                    if (newMovement != Movement.NONE) {
                        currentMovement = newMovement
                    }
                    false
                }
                .fillMaxHeight()
        ) {
            Text("Frame = ${updatedFrame.value}")
            for (row in 0 until SWITCH_ROWS) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(enabled = false),
                ) {
                    for (column in 0 until SWITCH_COLUMNS) {
                        pixelCallback.drawForPixel(column, row)
                    }
                }
            }
        }
    }

    @Composable
    private fun getPixel(checked: Boolean = false) {
        Switch(
            checked = checked,
            onCheckedChange = {},
            modifier = Modifier.requiredSize(
                width = SWITCH_WIDTH,
                height = SWITCH_HEIGHT,
            ).padding(all = SWITCH_PADDING),
        )
    }

    private fun isSnakeOnPixel(x: Int, y: Int): Boolean =
        snake.any { part ->
            part.positionX == x
                    && part.positionY == y
        }

    private fun generateApple() {
        while (apple == null) {
            val randomX = Random.nextInt(0, SWITCH_COLUMNS)
            val randomY = Random.nextInt(0, SWITCH_ROWS)

            if (isSnakeOnPixel(randomX, randomY).not()) {
                apple = Entity.Apple(randomX, randomY)
            }
        }
    }
}