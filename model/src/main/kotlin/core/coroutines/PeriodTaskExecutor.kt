package core.coroutines

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.*
import java.time.Instant

class PeriodTaskExecutor {
    private var nextUpdateTime: Long = 0
    private var periodMillis: Long = 0
    private var remainingTimeToNextUpdate: Long = 0

    private var isRunning = false
    private var isPause = false

    private var task: suspend () -> Unit = {}
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    init {
        initVars()
    }

    private fun initVars() {
        nextUpdateTime = 0
        periodMillis = 1000
        remainingTimeToNextUpdate = 0
    }

    fun scheduleTask(periodSec: Float, task: suspend () -> Unit) {
        if (isRunning) throw IllegalStateException("Stop executor before schedule task")
        this.task = task
        this.periodMillis = (periodSec * 1000).toLong()
        coroutineScope.launch { updateLoop() }
    }

    private suspend fun updateLoop() {
        isRunning = true
        while (isRunning) {
            executeTaskOnUpdateTime()
        }
    }

    private suspend fun executeTaskOnUpdateTime() {
        if (!isPause) {
            if (isItTimeToUpdate()) {
                Logger.log(Thread.currentThread().id.toString(), Log.Level.DEBUG)
                Logger.log("execute task", Log.Level.DEBUG)
                task()
            }
        }
        delay(SLEEP_INTERVAL)
    }

    private fun isItTimeToUpdate(): Boolean {
        val now = Instant.now().toEpochMilli()
        if (nextUpdateTime == 0L) {
            nextUpdateTime = now + periodMillis
        }
        if (now < nextUpdateTime)
            return false
        nextUpdateTime += periodMillis
        return true
    }


    fun pause() {
        isPause = true
        remainingTimeToNextUpdate = nextUpdateTime - Instant.now().toEpochMilli()

    }

    private fun stopUpdateLoop() {
        isRunning = false
        coroutineScope.cancel()
    }

    fun resume() {
        nextUpdateTime = Instant.now().toEpochMilli() + remainingTimeToNextUpdate
        isPause = false
    }

    fun stop() {
        stopUpdateLoop()
        initVars()
    }

    companion object {
        private const val SLEEP_INTERVAL: Long = 1
    }
}