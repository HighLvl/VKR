package app.services.scene.factory.entities

import core.entities.Agent

open class AgentImpl(override val agentType: String) : Agent, EntityImpl()