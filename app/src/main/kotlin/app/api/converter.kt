package app.api

import app.api.dto.Error
import app.api.dto.Requests
import app.api.dto.Response
import app.api.dto.Responses
import app.requests.ResponseTypeReference
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import core.services.logger.Level
import core.services.logger.Logger
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.io.IOException

private val objectMapper = ObjectMapper(MessagePackFactory())

fun Requests.mapToBytes(): ByteArray {
    return objectMapper.writeValueAsBytes(this)
}

fun ByteArray.mapToSnapshot(): Responses {
    return objectMapper.readValue(this)
}

class ResponsesDeserializer @JvmOverloads constructor(vc: Class<*>? = null) :
    StdDeserializer<Responses>(vc) {
    private val typeReference = ResponseTypeReference
    private val objectMapper = ObjectMapper(MessagePackFactory())

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): Responses {
        val responsesNode: ObjectNode = jp.codec.readTree(jp)
        val responsesList = mutableListOf<Response>()
        val responsesArrayNode = responsesNode["responses"]
        responsesArrayNode.elements().forEach { node ->
            kotlin.runCatching {
                val ack = node["ack"].asInt()
                val success = node["success"].asBoolean()
                val valueNode = node["value"]
                val value = when (success) {
                    true -> {
                        val type = typeReference.getTypeFor(ack).java
                        if (type == Unit::class) Unit else objectMapper.convertValue(valueNode, type)
                    }
                    false -> Error(valueNode["code"].asInt(), valueNode["text"].asText())
                }
                responsesList.add(Response(ack, success, value))
            }.onFailure {
                Logger.log("An error occurred while deserializing response\n ${it.message.orEmpty()}", Level.ERROR)
            }
        }
        return Responses(responsesList)
    }
}



