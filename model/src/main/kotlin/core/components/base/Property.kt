package core.components.base

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.IOException

@JsonDeserialize(using = PropertyDeserializer::class)
data class Property(val name: String, val type: Class<*>, val value: Any)

private class PropertyDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Property>(vc) {
    private val objectMapper = jacksonObjectMapper()
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): Property {
        val node: JsonNode = jp.codec.readTree(jp)
        val name = node["name"].asText()
        val type = objectMapper.convertValue(node["type"], Class::class.java)
        val value = objectMapper.convertValue(node["value"], type)
        return Property(name, type, value)
    }
}