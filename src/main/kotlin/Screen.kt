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


    private var apple: Entity.Apple? = null
    private var snake = Snake()
    private var currentMovement: Movement = Movement.RIGHT
    private var movedAtLeastOnce = false
    private lateinit var gameLoop: Loop
    private lateinit var updatedFrame: MutableState<Int>
    private val keyboardHandler = KeyboardHandler()
    private val focusRequester = FocusRequester()

    init {
        snake.moveHead(SWITCH_ROWS / 2, SWITCH_COLUMNS / 2)
        snake.addPart()
        snake.addPart()
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
    }

    @Composable
    private fun getFrame() {
        println("parseScreen")
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent(::keyHandler)
                .onKeyEvent(::keyHandler)
                .fillMaxHeight()
        ) {
            Text("Frame = ${updatedFrame.value}")
            for (row in 0 until SWITCH_ROWS) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    for (column in 0 until SWITCH_COLUMNS) {
                        val snakeOnPixel = snake.isOnPixel(column, row)
                        if (snakeOnPixel) {
                            getPixel(true)
                        } else if (apple?.positionX == column
                            && apple?.positionY == row){
                            getPixel(true)
                        } else {
                            getPixel(false)
                        }
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            while (true) {
                withFrameMillis { frame ->
                    focusRequester.requestFocus()
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

    private fun update() {
        println("update = ${updatedFrame.value}")
        movedAtLeastOnce = true
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
                if (snake.head.positionY == 0) {
                    canMove = false
                }
            }
            Movement.RIGHT -> {
                if (snake.head.positionX == SWITCH_COLUMNS) {
                    canMove = false
                }
            }
            Movement.DOWN -> {
                if (snake.head.positionY == SWITCH_ROWS) {
                    canMove = false
                }
            }
            Movement.LEFT -> {
                if (snake.head.positionX == 0) {
                    canMove = false
                }
            }
            else -> throw IllegalStateException("""
                checkMove => Movement should be one of these: 
                    - UP,
                    - RIGHT,
                    - DOWN,
                    - LEFT
            """.trimIndent()
            )
        }
        return canMove
    }

    private fun moveSnake() {
        snake.moveSnake(currentMovement)
    }

    private fun checkApple() {
        if (apple != null) {
            if (snake.isOnPixel(apple!!.positionX, apple!!.positionY)) {
                snake.addPart()
                apple = null
                generateApple()
            }
        }
    }

    private fun keyHandler(event: KeyEvent) : Boolean {
        val newMovement = keyboardHandler.handleKeyEventForMovement(keyEvent = event)
        if (newMovement != Movement.NONE
            && newMovement.illegalMovement() != currentMovement
            && movedAtLeastOnce) {
            currentMovement = newMovement
            movedAtLeastOnce = false
        }
        return false
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

    private fun generateApple() {
        while (apple == null) {
            val randomX = Random.nextInt(0, SWITCH_COLUMNS)
            val randomY = Random.nextInt(0, SWITCH_ROWS)

            if (snake.isOnPixel(randomX, randomY).not()) {
                apple = Entity.Apple(randomX, randomY)
            }
        }
    }
}