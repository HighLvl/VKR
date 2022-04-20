package app.services.scene.factory.entities

import core.entities.AgentPrototype

class AgentPrototypeImpl(override val agentType: String) : AgentPrototype, AgentImpl(agentType)