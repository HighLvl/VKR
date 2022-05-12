package components.optimization.opt4j

import components.optimization.optimizer.Problem
import org.opt4j.core.genotype.DoubleGenotype
import org.opt4j.core.problem.Decoder

class DoubleDecoder : Decoder<StepDoubleGenotype, List<Double>> {
    private val creator = DoubleCreator()
    override fun decode(genotype: StepDoubleGenotype): List<Double> {
        genotype.roundUpToStep()
        var validGenotype = genotype
        while (!Problem.experiment.isValidDecision(validGenotype)) {
            validGenotype = creator.create()
        }
        return validGenotype
    }


}