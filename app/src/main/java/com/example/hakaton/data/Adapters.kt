package com.example.hakaton.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

val DATE_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter
    .ofPattern("uuuu-MM-dd")
    .withResolverStyle(ResolverStyle.STRICT)
val DATE_ONLY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")

class LocalDateAdapter : TypeAdapter<LocalDate>() {

    // приложение -> сервер: "2026-11-19"
    override fun write(out: JsonWriter, value: LocalDate?) {
        if (value == null) out.nullValue() else out.value(value.toString())
    }

    // сервер -> приложение: "2026-11-19"
    override fun read(reader: JsonReader): LocalDate? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return LocalDate.parse(reader.nextString().take(10))
    }
}