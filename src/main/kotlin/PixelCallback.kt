import androidx.compose.runtime.Composable

interface PixelCallback {
    @Composable
    fun drawForPixel(column: Int, row: Int)
}