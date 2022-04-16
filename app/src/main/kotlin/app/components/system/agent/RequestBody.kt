package app.components.system.agent

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.IOException

@JsonDeserialize(using = RequestBodyDeserializer::class)
data class RequestBody(val name: String, val args: Map<String, Pair<Any, Class<*>>>)

private class RequestBodyDeserializer @JvmOverloads constructor(vc: Class<*>? = null) :
    StdDeserializer<RequestBody>(vc) {
    private val objectMapper = jacksonObjectMapper()

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): RequestBody {
        val node: JsonNode = jp.codec.readTree(jp)
        val name = node["name"].asText()
        val args = mutableMapOf<String, Pair<Any, Class<*>>>()
        node["args"].fields().forEach { (argName, valueTypePair) ->
            val type = objectMapper.convertValue(valueTypePair["second"], Class::class.java)
            val value = objectMapper.convertValue(valueTypePair["first"], type)
            args[argName] = Pair(value, type)
        }
        return RequestBody(name, args)
    }
}