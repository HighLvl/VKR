package components.optimization.opt4j

import org.opt4j.core.problem.ProblemModule

class DoubleProblemModule : ProblemModule() {
    override fun config() {
        bindProblem(DoubleCreator::class.java, DoubleDecoder::class.java, DoubleEvaluator::class.java)
    }
}