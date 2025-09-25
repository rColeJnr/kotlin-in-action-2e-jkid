package kia.jkid.serialization

import kia.jkid.CustomSerializer
import kia.jkid.JsonExclude
import kia.jkid.JsonName
import kia.jkid.ValueSerializer
import kia.jkid.exercise.DateFormat
import kia.jkid.exercise.DateSerializer
import kia.jkid.joinToStringBuilder
import java.text.SimpleDateFormat
import java.util.Date
import javax.swing.text.SimpleAttributeSet
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

fun serialize(obj: Any): String = buildString { serializeObject(obj) }

/* the first implementation discussed in the book */
//private fun StringBuilder.serializeObjectWithoutAnnotation(obj: Any) {
//    val kClass = obj::class as KClass<Any>
//    val properties = kClass.memberProperties
//
//    properties.joinToStringBuilder(this, prefix = "{", postfix = "}") { prop ->
//        serializeString(prop.name)
//        append(": ")
//        serializePropertyValue(prop.get(obj))
//    }
//}

/**
 * Takes object of type Any, gets the KClass and calls for memberProperties,
 * memberProperties returns a list of all properties in this class and superclass
 * Lists are collections, therefor we can call extension functions of the collection type
 * the library extends the Iterable interface to define .joinToStringBuilder - which just joinsToString.
 * serializeProperty()
 */


private fun StringBuilder.serializeObject(obj: Any) {
    (obj::class as KClass<Any>)
        .memberProperties
        .filter { it.findAnnotation<JsonExclude>() == null }
        .joinToStringBuilder(this, prefix = "{", postfix = "}") {
            serializeProperty(it, obj)
        }
}

/**
 * Each property is of type KProperty1<Any, *>
 * star-projector R bcs the generic type has been erased.
 * having pro
 */
private fun StringBuilder.serializeProperty(
    prop: KProperty1<Any, *>, obj: Any
) {
    val jsonNameAnn = prop.findAnnotation<JsonName>()
    val propName = jsonNameAnn?.name ?: prop.name
    serializeString(propName)
    append(": ")

    val value = prop.get(obj)
    // calls the getter of this property.   the way it works, is:
    /*
    Given:
    data class Person(var name: String, var age: Int) - accessors are defined for both of them

    var person = Person("ALicia", 21)
    val kClass = ::person  - reference to this person instance KClass, and the values in its properties, bcs u get access to the properties and then the getters and setters ...
    fun Person.age() = age
    println(::person.age())
    // 21
// this is all wrong btw.
     */
    val jsonValue = prop.getSerializer()?.toJsonValue(value) ?: value
    serializePropertyValue(jsonValue)
}

fun KProperty<*>.getSerializer(): ValueSerializer<Any?>? {
    val jsonDateAnn = findAnnotation<DateFormat>()
    if (jsonDateAnn != null) {
        return DateSerializer(jsonDateAnn.format) as ValueSerializer<Any?>
    }
    val customSerializerAnn = findAnnotation<CustomSerializer>() ?: return null
    val serializerClass = customSerializerAnn.serializerClass

    val valueSerializer = serializerClass.objectInstance
        ?: serializerClass.createInstance()
    @Suppress("UNCHECKED_CAST")
    return valueSerializer as ValueSerializer<Any?>
}

private fun StringBuilder.serializePropertyValue(value: Any?) {
    when (value) {
        null -> append("null")
        is String -> serializeString(value)
        is Number, is Boolean -> append(value.toString())
        is List<*> -> serializeList(value)
        is Map<*,*> -> serializeMap(value)
        else -> serializeObject(value)
    }
}

private fun StringBuilder.serializeMap(data: Map<*, *>) {
    data.toList().joinToStringBuilder(this, prefix = "{", postfix = "}") { entry ->
        val (key, value) = entry
        serializeString(key.toString())
        append(": ")
        serializePropertyValue(value)
    }
}

private fun StringBuilder.serializeList(data: List<Any?>) {
    data.joinToStringBuilder(this, prefix = "[", postfix = "]") {
        serializePropertyValue(it)
    }
}

private fun StringBuilder.serializeString(s: String) {
    append('\"')
    s.forEach { append(it.escape()) }
    append('\"')
}

private fun Char.escape(): Any =
    when (this) {
        '\\' -> "\\\\"
        '\"' -> "\\\""
        '\b' -> "\\b"
        '\u000C' -> "\\f"
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\t' -> "\\t"
        else -> this
    }
