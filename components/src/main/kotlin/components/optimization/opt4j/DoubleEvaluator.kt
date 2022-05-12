package components.optimization.opt4j

import components.optimization.optimizer.Problem
import org.opt4j.core.Objective
import org.opt4j.core.Objectives
import org.opt4j.core.problem.Evaluator

class DoubleEvaluator :
    Evaluator<List<Double>> {
    override fun evaluate(phenotype: List<Double>): Objectives {
        makeDecision(phenotype)
        val objectives = Objectives().apply {
            val tfValue = Problem.getTargetFunctionValue()
            add("obj", Objective.Sign.MAX, tfValue)
        }
        return objectives
    }

    private fun makeDecision(phenotype: List<Double>) {
        Problem.inputParams.forEachIndexed { index, input ->
            input.value = phenotype[index]
        }
        Problem.makeDecision()
    }
}