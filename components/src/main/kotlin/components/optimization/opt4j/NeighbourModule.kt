package components.optimization.opt4j

import org.opt4j.operators.neighbor.NeighborModule

class NeighbourModule : NeighborModule() {
    override fun configure() {
        super.configure()
        addOperator(StepNeighbourDouble::class.java)
    }

    override fun config() {
        bind(StepNeighbourDouble::class.java).`in`(SINGLETON)
    }
}