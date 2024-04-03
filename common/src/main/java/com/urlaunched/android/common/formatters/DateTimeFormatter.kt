package com.urlaunched.android.common.formatters

import android.content.Context
import android.text.format.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale

object DateTimeFormatter {
    fun formatStringToLocalDate(value: String): LocalDate? {
        return try {
            // Parses the input string as an Instant (UTC time)
            val instant = Instant.parse(value)
            // Converts Instant to LocalDateTime in system default time zone
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            return localDateTime.toLocalDate()
        } catch (exception: DateTimeParseException) {
            null
        }
    }

    fun formatStringToLocalDateTime(value: String): LocalDateTime? = try {
        val instant = Instant.parse(value)
        instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    } catch (exception: DateTimeParseException) {
        null
    }

    fun formatLocalDate(value: LocalDate, pattern: String = DATE_PRESENTATION_VALUE): String =
        value.format(DateTimeFormatter.ofPattern(pattern))

    fun formatLocalDateTime(
        value: LocalDateTime,
        context: Context,
        pattern: String = DATE_TIME_PRESENTATION_VALUE_12_H,
        pattern24Hours: String = DATE_TIME_PRESENTATION_VALUE_24_H
    ): String = value.format(
        DateTimeFormatter.ofPattern(
            if (DateFormat.is24HourFormat(context)) {
                pattern24Hours
            } else {
                pattern
            }
        )
    )

    fun formatLocalDateTime(value: LocalDateTime, style: FormatStyle = FormatStyle.MEDIUM): String = value.format(
        DateTimeFormatter
            .ofLocalizedDate(style)
            .withLocale(Locale.getDefault())
    )

    fun formatLocalDate(value: LocalDate, style: FormatStyle): String = value.format(
        DateTimeFormatter
            .ofLocalizedDate(style)
            .withLocale(Locale.getDefault())
    )

    private const val DATE_TIME_PRESENTATION_VALUE_24_H = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_TIME_PRESENTATION_VALUE_12_H = "yyyy-MM-dd HH:mm:ss a"
    private const val DATE_PRESENTATION_VALUE = "dd/MM/yyyy"
    const val DATE_PLACEHOLDER = "—"
}