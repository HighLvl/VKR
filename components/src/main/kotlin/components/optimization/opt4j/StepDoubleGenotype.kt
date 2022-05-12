package components.optimization.opt4j

import components.optimization.optimizer.Problem
import org.opt4j.core.genotype.Bounds
import org.opt4j.core.genotype.DoubleGenotype
import java.util.*

class StepDoubleGenotype(bounds: Bounds<Double>) : DoubleGenotype(bounds) {

    fun getStep(i: Int): Double {
        return Problem.inputParams[i].step
    }

    override fun init(random: Random, n: Int) {
        super.init(random, n)
        roundUpToStep()
    }

    fun roundUpToStep() {
        for (i in this.indices) {
            val gene = this[i]
            val step = getStep(i)
            this[i] = Companion.roundUpToStep(gene, step)
        }
    }
    companion object {
        fun roundUpToStep(value: Double, step: Double): Double {
            val mod = value % step
            if (mod > step / 2) {
                return value - mod + step
            }
            return value - mod
        }

    }
}