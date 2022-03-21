package app.api

import app.api.dto.*
import app.components.configuration.AgentInterfaces
import app.components.getSnapshot
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import core.entities.getComponent
import core.services.Services
import org.msgpack.jackson.dataformat.MessagePackFactory

private val objectMapper = ObjectMapper(MessagePackFactory())

fun InputData.mapToBytes(): ByteArray {
    val inputDataList = requests.map {
        listOf(it.agentId, it.name, it.ack, it.args)
    }.toList()
    return objectMapper.writeValueAsBytes(inputDataList)
}

fun ByteArray.mapToSnapshot(): Snapshot {
    val comp = Services.scene.environment.getComponent<AgentInterfaces>()!!
    val agentInterfaces = comp.agentInterfaces
    val snapshotList = objectMapper.readValue<List<Any>>(this)
    val t = snapshotList[0] as Double
    val agentSnapshots = (snapshotList[1] as List<*>).map {
        it as List<*>
        val type = it[0] as String
        (it[1] as List<*>).map { snapshot ->
            snapshot as List<*>
            val id = snapshot[0] as Int
            val propNames = agentInterfaces[type]!!.properties.map {prop -> prop.name }
            val props = (snapshot[1] as List<Any>).mapIndexed { index, prop -> propNames[index] to prop }.toMap()
            AgentSnapshot(type, id, props)
        }
    }.flatten()
    val responses = (snapshotList[2] as List<*>).map {
        it as List<*>
        val ack = it[0] as Int
        val isSuccess = it[1] as Boolean
        val value = if (it.size == 2) Unit else it[2] as Any
        val result: Result<Any> = when {
            isSuccess -> Result.success(value)
            else -> {
                value as List<*>
                Result.failure<Error>(Error(value[0] as Int, value[1] as String))
            }
        }
        Response(ack, result)
    }
    val state = when (snapshotList[3] as Int) {
        0 -> State.RUN
        1 -> State.STOP
        2 -> State.PAUSE
        else -> throw IllegalStateException()
    }
    return Snapshot(t, agentSnapshots, responses, state)
}