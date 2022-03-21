package app.services.model.control

import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PeriodTaskExecutor(context: CoroutineContext) {
    private var periodMillis: Long = 100
    private var remainingTimeToNextUpdate: Long = 0
    private var startTime: Long = 0
    private var isPause = false
    private var task: suspend () -> Unit = {}
    private val coroutineScope = CoroutineScope(context)
    private var isStopped = true

    private var nextUpdateTime: Long = 0L
        get() {
            if (field == 0L) {
                field = System.currentTimeMillis() + periodMillis
            }
            return field
        }

    private fun initVars() {
        nextUpdateTime = 0
        remainingTimeToNextUpdate = 0
        isPause = false
        isStopped = false
        task = {}
    }

    fun scheduleTask(pause: Boolean = false, task: suspend () -> Unit) {
        if (!isStopped) return
        initVars()
        if (pause) pause()
        this.task = task
        coroutineScope.launch { updateLoop() }
    }

    fun changePeriod(periodSec: Float) {
        periodMillis = (periodSec * 1000).toLong()
        nextUpdateTime = startTime + periodMillis
    }

    private suspend fun updateLoop() {
        while (!isStopped) {
            executeTaskOnUpdateTime()
            yield()
        }
    }

    private suspend fun executeTaskOnUpdateTime() {
        if (!isPause) {
            if (isItTimeToUpdate()) {
                task()
                startTime = System.currentTimeMillis()
                nextUpdateTime = startTime + periodMillis
            }
        }
    }

    private fun isItTimeToUpdate(): Boolean {
        val now = System.currentTimeMillis()
        if (now < nextUpdateTime)
            return false
        return true
    }


    fun pause() {
        isPause = true
        remainingTimeToNextUpdate = nextUpdateTime - System.currentTimeMillis()
        Logger.log("Remaining time to next update: $remainingTimeToNextUpdate", Level.DEBUG)
    }

    fun resume() {
        nextUpdateTime = System.currentTimeMillis() + remainingTimeToNextUpdate
        isPause = false
    }

    fun start() {
        scheduleTask(isPause, task)
    }

    fun stop() {
        isStopped = true
    }

    companion object {
        private const val SLEEP_INTERVAL: Long = 1
    }
}