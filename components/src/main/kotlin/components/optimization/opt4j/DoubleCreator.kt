package components.optimization.opt4j

import components.optimization.optimizer.Problem
import core.components.experiment.OptimizationExperiment
import org.opt4j.core.genotype.DoubleBounds
import org.opt4j.core.genotype.DoubleGenotype
import org.opt4j.core.problem.Creator

class DoubleCreator : Creator<StepDoubleGenotype> {
    private val random = java.util.Random()
    private val inputParams: List<OptimizationExperiment.Input>
        get() = Problem.inputParams
    private val bounds = DoubleBounds(inputParams.map { it.minValue }, inputParams.map { it.maxValue })

    override fun create(): StepDoubleGenotype {
        val genotype = StepDoubleGenotype(bounds)
        genotype.init(random, inputParams.size)
        return genotype
    }
}