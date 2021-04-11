import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

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

    fun getWindowSize() = IntSize(
        width = screenWidth.roundToInt(),
        height = screenHeight.roundToInt()
    )

    @Composable
    fun getScreen() {
        parseScreen(object:PixelCallback {
            @Composable
            override fun drawForPixel(column: Int, row: Int) {
                getPixel(checked = (row + column) % 2 == 0)
            }
        })
    }

    @Composable
    private fun parseScreen(pixelCallback: PixelCallback) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxHeight()
        ) {
            for (row in 0 until SWITCH_ROWS) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
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
}