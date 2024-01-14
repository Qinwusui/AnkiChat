package com.wusui.config

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import org.ktorm.jackson.KtormModule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object JacksonConfig {
	fun ObjectMapper.config(
		localDatePattern: String,
		localTimePattern: String,
		localDateTimePattern: String
	): ObjectMapper {
		registerModule(KtormModule())
		registerModule(JavaTimeModule().apply {
			addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ofPattern(localDatePattern)))
			addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ofPattern(localDatePattern)))
			addDeserializer(LocalTime::class.java, LocalTimeDeserializer(DateTimeFormatter.ofPattern(localTimePattern)))
			addSerializer(LocalTime::class.java, LocalTimeSerializer(DateTimeFormatter.ofPattern(localTimePattern)))
			addDeserializer(
				LocalDateTime::class.java,
				LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(localDateTimePattern))
			)
			addSerializer(
				LocalDateTime::class.java,
				LocalDateTimeSerializer(DateTimeFormatter.ofPattern(localDateTimePattern))
			)
		})
		configure(SerializationFeature.INDENT_OUTPUT, true)
		setDefaultLeniency(true)
		setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
			indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
			indentObjectsWith(DefaultIndenter("  ", "\n"))
		})
		return this
	}

}