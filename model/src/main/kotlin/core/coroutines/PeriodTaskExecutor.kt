package core.coroutines

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class PeriodTaskExecutor {
    private var nextUpdateTime: Long = 0L
        get() {
            if (field == 0L) {
                field = System.currentTimeMillis() + periodMillis
            }
            return field
        }

    private var periodMillis: Long = 100
    private var remainingTimeToNextUpdate: Long = 0
    private var startTime: Long = 0
    private var isPause = false

    private var task: suspend () -> Unit = {}

    @OptIn(DelicateCoroutinesApi::class)
    private val coroutineScope = CoroutineScope(newSingleThreadContext(PeriodTaskExecutor::class.qualifiedName.toString()))
    private var updateLoopJob: Job? = null

    private var isStopped = false

    init {
        initVars()
    }

    private fun initVars() {
        nextUpdateTime = 0
        remainingTimeToNextUpdate = 0
        isPause = false
        updateLoopJob = null
        task = {}
    }

    fun scheduleTask(task: suspend () -> Unit) {
        if (updateLoopJob != null) return
        this.task = task
        this.updateLoopJob = coroutineScope.launch { updateLoop() }
    }

    fun changePeriod(periodSec: Float) {
        periodMillis = (periodSec * 1000).toLong()
        nextUpdateTime = startTime + periodMillis
    }

    private suspend fun updateLoop() {
        while (!isStopped) {
            executeTaskOnUpdateTime()
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
        yield()
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
        Logger.log("Remaining time to next update: $remainingTimeToNextUpdate", Log.Level.DEBUG)
    }

    fun resume() {
        nextUpdateTime = System.currentTimeMillis() + remainingTimeToNextUpdate
        isPause = false
    }

    fun stop() {
        isStopped = false
        updateLoopJob?.cancel()
        initVars()
    }

    companion object {
        private const val SLEEP_INTERVAL: Long = 1
    }
}