package app.services.scene.factory

import app.components.getSnapshot
import app.components.loadSnapshot
import app.components.system.agent.AgentInterface
import app.components.system.experiment.common.Experiment
import app.requests.RequestSender
import app.services.scene.factory.entities.AgentImpl
import app.services.scene.factory.entities.AgentPrototypeImpl
import app.services.scene.factory.entities.EnvironmentImpl
import app.services.scene.factory.entities.ExperimenterImpl
import core.components.configuration.MutableRequestSignature
import core.entities.*

object EntityFactory {
    fun createExperimenter(): Experimenter {
        val experimenter = ExperimenterImpl()
        experimenter.setComponent<Experiment>()
        return experimenter
    }

    fun createEnvironment(): Environment {
        return EnvironmentImpl()
    }

    fun createAgent(
        agentPrototype: AgentPrototype,
        setterSignatures: List<MutableRequestSignature>,
        otherRequestSignatures: List<MutableRequestSignature>,
        requestSender: RequestSender
    ): Agent {
        return AgentImpl(agentPrototype.agentType).apply {
            with(setComponent<AgentInterface>()) {
                setRequestSignatures(setterSignatures + otherRequestSignatures)
                setRequestSender(requestSender)
            }
            agentPrototype.getComponents().forEach {
                setComponent(it::class).loadSnapshot(it.getSnapshot())
            }
        }
    }

    fun createAgentPrototype(agentType: String): AgentPrototype {
        return AgentPrototypeImpl(agentType)
    }
}