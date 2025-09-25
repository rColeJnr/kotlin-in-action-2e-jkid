package kia.jkid.exercise

import kia.jkid.CustomSerializer
import kia.jkid.ValueSerializer
import kia.jkid.deserialization.JKidException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.PROPERTY)
annotation class DateFormat(val format: String)

class DateSerializer(format: String) : ValueSerializer<Date> {
    val simpleDateFormat = SimpleDateFormat(format)

    override fun fromJsonValue(jsonValue: Any?): Date {
        if (jsonValue !is String) throw JKidException("Expected string, was: $jsonValue")
        return simpleDateFormat.parse(jsonValue)
    }

    override fun toJsonValue(value: Date) = simpleDateFormat.format(value)
}