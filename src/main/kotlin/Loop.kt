class Loop {
    companion object {
        private const val FRAME_TIME = 500L
    }

    private var lastUpdatedTime = System.currentTimeMillis()
    private var canUpdate = true

    fun shouldUpdate(): Boolean {
        if (canUpdate) {
            val elapsedTime = System.currentTimeMillis()
            return elapsedTime - lastUpdatedTime > FRAME_TIME
        }
        return false
    }

    fun markAsUpdated() {
        if (canUpdate) {
            lastUpdatedTime = System.currentTimeMillis()
        }
    }

    fun stopUpdating() {
        canUpdate = false
    }
}