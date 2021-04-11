import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.random.Random

class Game {

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
    private var gameLoop = Loop()
    private val keyboardHandler = KeyboardHandler()
    private val gameFocusRequester = FocusRequester()
    private val lostFocusRequester = FocusRequester()
    private var stayAwake = mutableStateOf(0L)
    private var score = mutableStateOf(0L)

    init {
        start()
    }

    fun getWindowSize() = IntSize(
        width = screenWidth.roundToInt(),
        height = screenHeight.roundToInt()
    )

    @Composable
    fun getScreen() {
        score = remember {
            mutableStateOf(0)
        }

        getFrame()
    }

    @Composable
    private fun getFrame() {
        if (gameLoop.gameState == Loop.State.LOST) {
            Column {
                Text(
                    "You lost, press [r] to restart",
                    modifier = Modifier
                        .focusRequester(lostFocusRequester)
                        .focusable()
                        .onPreviewKeyEvent(::keyHandler)
                        .fillMaxSize()
                )
                Text(
                    "${stayAwake.value}",
                    modifier = Modifier.requiredSize(0.dp)
                )
            }

            LaunchedEffect(Unit) {
                while (true) {
                    withFrameMillis {
                        lostFocusRequester.requestFocus()
                        if (stayAwake.value > 10) {
                            stayAwake.value = 0
                        }
                        stayAwake.value += it
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .focusRequester(gameFocusRequester)
                    .focusable()
                    .onPreviewKeyEvent(::keyHandler)
                    .fillMaxSize()
            ) {
                Text("Score = ${score.value}")
                for (row in 0 until SWITCH_ROWS) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        for (column in 0 until SWITCH_COLUMNS) {
                            val snakeOnPixel = snake.isOnPixel(column, row)
                            if (snakeOnPixel) {
                                getPixel(true, snake.head)
                            } else if (apple?.position?.x == column
                                && apple?.position?.y == row
                            ) {
                                getPixel(true, apple!!)
                            } else {
                                getPixel(false, Entity.None)
                            }
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                while (true) {
                    withFrameMillis {
                        gameFocusRequester.requestFocus()
                        if (gameLoop.shouldUpdate()) {
                            score.value += 1
                            update()
                            gameLoop.markAsUpdated()
                        }
                    }
                }
            }
        }
    }

    private fun start() {
        snake.start(
            Position(
                x = SWITCH_ROWS / 2,
                y = SWITCH_COLUMNS / 2
            )
        )
        apple = null
        generateApple()

        movedAtLeastOnce = false
        score.value = 0
        currentMovement = Movement.RIGHT
        gameLoop.gameState = Loop.State.RUNNING
    }

    private fun update() {
        movedAtLeastOnce = true
        val moveCollided = moveCollided()
        if (moveCollided) {
            gameLoop.gameState = Loop.State.LOST
        } else {
            moveSnake()
            checkApple()
        }
    }

    private fun moveCollided(): Boolean {
        val head = snake.nextHeadPosition(currentMovement)
        val checkWalls = checkMoveWithWalls(head)
        val checkParts = snake.headCollidesWithParts(head)

        return checkWalls || checkParts
    }

    private fun checkMoveWithWalls(position: Position): Boolean {
        var checkWalls = false
        when (currentMovement) {
            Movement.UP -> {
                if (position.y < 0) {
                    checkWalls = true
                }
            }
            Movement.RIGHT -> {
                if (position.x == SWITCH_COLUMNS) {
                    checkWalls = true
                }
            }
            Movement.DOWN -> {
                if (position.y == SWITCH_ROWS) {
                    checkWalls = true
                }
            }
            Movement.LEFT -> {
                if (position.x < 0) {
                    checkWalls = true
                }
            }
            else -> throw IllegalStateException(
                """
                checkMove => Movement should be one of these: 
                    - UP,
                    - RIGHT,
                    - DOWN,
                    - LEFT
            """.trimIndent()
            )
        }
        return checkWalls
    }

    private fun moveSnake() {
        snake.moveSnake(currentMovement)
    }

    private fun checkApple() {
        if (apple != null) {
            if (snake.isOnPixel(apple!!.position)) {
                snake.addPart()
                score.value += 10
                apple = null
                generateApple()
            }
        }
    }

    private fun keyHandler(event: KeyEvent): Boolean {
        val newMovement = keyboardHandler.handleKeyEventForMovement(keyEvent = event)
        if (newMovement != Movement.NONE
            && newMovement.illegalMovement() != currentMovement
            && movedAtLeastOnce
        ) {
            currentMovement = newMovement
            movedAtLeastOnce = false
        }

        when (keyboardHandler.handleKeyEventForActions(keyEvent = event)) {
            Actions.PAUSE -> gameLoop.pauseUnpause()
            Actions.RETRY -> {
                if (gameLoop.gameState == Loop.State.LOST) {
                    start()
                }
            }
            Actions.NONE -> {
                // Do nothing
            }
        }
        return false
    }

    @Composable
    private fun getPixel(checked: Boolean = false, entityType: Entity) {
        val color = when (entityType) {
            is Entity.Snake -> SwitchDefaults.colors(
                checkedThumbColor = Color(
                    red = 27,
                    green = 94,
                    blue = 32,
                )
            )
            is Entity.Apple -> SwitchDefaults.colors(
                checkedThumbColor = Color(
                    red = 183,
                    green = 28,
                    blue = 28
                )
            )
            Entity.None -> SwitchDefaults.colors(
                checkedThumbColor = Color.Gray
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = {},
            modifier = Modifier.requiredSize(
                width = SWITCH_WIDTH,
                height = SWITCH_HEIGHT,
            ).padding(all = SWITCH_PADDING),
            colors = color
        )
    }

    private fun generateApple() {
        while (apple == null) {
            val randomPosition = Position(
                x = Random.nextInt(0, SWITCH_COLUMNS),
                y = Random.nextInt(0, SWITCH_ROWS)
            )
            if (snake.isOnPixel(randomPosition).not()) {
                apple = Entity.Apple(
                    randomPosition
                )
            }
        }
    }
}