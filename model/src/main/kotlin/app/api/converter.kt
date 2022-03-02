package app.api

import Model.AgentBehaviour
import Model.Empty
import Model.ModelState
import Model.Request
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.flatbuffers.FlatBufferBuilder
import core.api.dto.*
import java.nio.ByteBuffer

private val objectMapper = jacksonObjectMapper()
private val flatBufferBuilder = FlatBufferBuilder()

fun ModelState.mapToStateTable() = when (this.state()) {
    Model.State.Run -> State.RUN
    Model.State.Stop -> State.STOP
    else -> State.PAUSE
}

fun GlobalArgs.mapToGlobalArgsTable(): Model.GlobalArgs {
    val jsonArgs = ByteBuffer.wrap(objectMapper.writeValueAsBytes(this.args))
    return createGlobalArgs(jsonArgs)
}

private fun createGlobalArgs(jsonArgs: ByteBuffer): Model.GlobalArgs {
    flatBufferBuilder.clear()
    val offset = Model.GlobalArgs.createGlobalArgs(flatBufferBuilder, flatBufferBuilder.createString(jsonArgs))
    flatBufferBuilder.finish(offset)
    return Model.GlobalArgs.getRootAsGlobalArgs(flatBufferBuilder.dataBuffer())
}


fun Behaviour.mapToBehaviourTable(): Model.Behaviour {
    flatBufferBuilder.clear()
    val agentBehaviourOffsets = this.requests.map { agentBehaviour ->
        val requestOffsets = agentBehaviour.requests.map { request ->
            val argsJson = ByteBuffer.wrap(objectMapper.writeValueAsBytes(request.args))
            createRequest(request.name, request.requestId, argsJson)
        }.toIntArray()
        createAgentBehaviour(agentBehaviour.id, requestOffsets)
    }.toIntArray()
    return createBehaviour(agentBehaviourOffsets)
}

private fun createRequest(requestName: String, requestId: Int?, argsJson: ByteBuffer): Int {
    Request.startRequest(flatBufferBuilder)
    requestId?.let { Request.addId(flatBufferBuilder, it) }
    Request.addName(flatBufferBuilder, flatBufferBuilder.createString(requestName))
    Request.addArgs(flatBufferBuilder, flatBufferBuilder.createString(argsJson))
    return Request.endRequest(flatBufferBuilder)
}

private fun createAgentBehaviour(
    id: Int,
    requestOffsets: IntArray
): Int {
    val vectorOfRequests = flatBufferBuilder.createVectorOfTables(requestOffsets)
    return AgentBehaviour.createAgentBehaviour(flatBufferBuilder, id, vectorOfRequests)
}

private fun createBehaviour(agentBehaviourOffsets: IntArray): Model.Behaviour {
    val vectorOfAgentBehaviours = flatBufferBuilder.createVectorOfTables(agentBehaviourOffsets)
    val offset = Model.Behaviour.createBehaviour(flatBufferBuilder, vectorOfAgentBehaviours)
    flatBufferBuilder.finish(offset)
    return Model.Behaviour.getRootAsBehaviour(flatBufferBuilder.dataBuffer())
}

fun Model.Snapshot.mapToSnapshot(): Snapshot {
    val time = time()
    val agentSnapshots = (0 until snapshotsLength()).asSequence()
        .map {
            snapshots(it)
        }.map { agentSnapshot ->
            agentSnapshot.mapToAgentSnapshot()
        }.toList()
    val errors = (0 until errorsLength()).asSequence()
        .map {
            errors(it)
        }.map { error ->
            Error(error.code(), error.text())
        }.toList()
    return Snapshot(time, agentSnapshots, errors)
}

private fun Model.AgentSnapshot.mapToAgentSnapshot(): AgentSnapshot {
    val props = objectMapper.readValue<Map<String, Any>>(props())
    val responses = (0 until responsesLength()).asSequence()
        .map { responseIndex ->
            responses(responseIndex)
        }.map { response ->
            val value = objectMapper.readValue<Any>(response.value())
            Response(response.id(), value)
        }.toList()
    return AgentSnapshot(type(), id(), props, responses)
}
