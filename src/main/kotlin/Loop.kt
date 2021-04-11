import Loop.State.*

class Loop {
    companion object {
        private const val FRAME_TIME = 100L
    }

    enum class State {
        RUNNING,
        PAUSED,
        LOST
    }

    private var lastUpdatedTime = System.currentTimeMillis()
    var gameState = RUNNING

    fun shouldUpdate(): Boolean {
        if (gameState == RUNNING) {
            val elapsedTime = System.currentTimeMillis()
            return elapsedTime - lastUpdatedTime > FRAME_TIME
        }
        return false
    }

    fun markAsUpdated() {
        if (gameState == RUNNING) {
            lastUpdatedTime = System.currentTimeMillis()
        }
    }

    fun pauseUnpause() {
        gameState = when (gameState) {
            RUNNING -> PAUSED
            PAUSED -> RUNNING
            LOST -> LOST
        }
    }
}