package app.api

import app.api.dto.Error
import app.api.dto.InputData
import app.api.dto.Response
import app.api.dto.Snapshot
import app.components.agent.RequestBody
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.io.IOException

private val objectMapper = ObjectMapper(MessagePackFactory())

fun InputData.mapToBytes(): ByteArray {
    return objectMapper.writeValueAsBytes(this)
}

fun ByteArray.mapToSnapshot(): Snapshot {
    return objectMapper.readValue(this)
}

class ResponseDeserializer @JvmOverloads constructor(vc: Class<*>? = null) :
    StdDeserializer<Response>(vc) {

    private val objectMapper = ObjectMapper(MessagePackFactory())
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): Response {
        val node: ObjectNode = jp.codec.readTree(jp)
        val ack = node["ack"].asInt()
        val success = node["success"].asBoolean()
        val valueNode = node["value"]
        val value  = when(success) {
            true -> objectMapper.convertValue(valueNode, Any::class.java)
            false -> Error(valueNode["code"].asInt(), valueNode["text"].asText())
        }
        return Response(ack, success, value)
    }
}



