package components.optimization.opt4j

import com.google.inject.Inject
import components.optimization.optimizer.Problem
import org.opt4j.core.common.random.Rand
import org.opt4j.operators.neighbor.Neighbor
import org.opt4j.operators.normalize.NormalizeDouble
import java.util.*

class StepNeighbourDouble @Inject constructor(private val normalize: NormalizeDouble, random: Rand) :
    Neighbor<StepDoubleGenotype> {
    private val random: Random = random

    override fun neighbor(genotype: StepDoubleGenotype) {
        val size = random.nextInt(genotype.size) + 1
        for (i in 0 until size) {
            val step = genotype.getStep(i)
            val change = (random.nextInt(Problem.maxStepDev) + 1) * step * if (random.nextBoolean()) -1 else 1
            val value = StepDoubleGenotype.roundUpToStep(
                genotype[i] + change,
                step
            )
            genotype[i] = value
        }
        normalize.normalize(genotype)
    }

}