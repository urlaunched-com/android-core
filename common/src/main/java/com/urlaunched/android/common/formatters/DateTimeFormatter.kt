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
    fun formatStringToLocalDate(value: String, zone: ZoneId = ZoneId.systemDefault()): LocalDate? {
        return try {
            // Parses the input string as an Instant (UTC time)
            val instant = Instant.parse(value)
            // Converts Instant to LocalDateTime in system default time zone
            val localDateTime = instant.atZone(zone).toLocalDateTime()
            return localDateTime.toLocalDate()
        } catch (exception: DateTimeParseException) {
            null
        }
    }

    fun formatStringToLocalDateTime(value: String, zone: ZoneId = ZoneId.systemDefault()): LocalDateTime? = try {
        val instant = Instant.parse(value)
        instant.atZone(zone).toLocalDateTime()
    } catch (exception: DateTimeParseException) {
        null
    }

    fun formatInstantToLocalDateTime(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): LocalDateTime {
        // Converts Instant to LocalDateTime in system default time zone
        val localDateTime = instant.atZone(zone).toLocalDateTime()
        return localDateTime
    }

    fun formatLocalDate(
        value: LocalDate,
        pattern: String = DATE_PRESENTATION_VALUE,
        locale: Locale = Locale.getDefault()
    ): String = value.format(DateTimeFormatter.ofPattern(pattern, locale))

    fun formatLocalDateTime(
        value: LocalDateTime,
        context: Context,
        pattern: String = DATE_TIME_PRESENTATION_VALUE_12_H,
        pattern24Hours: String = DATE_TIME_PRESENTATION_VALUE_24_H,
        locale: Locale = Locale.getDefault()
    ): String = value.format(
        DateTimeFormatter.ofPattern(
            if (DateFormat.is24HourFormat(context)) {
                pattern24Hours
            } else {
                pattern
            },
            locale
        )
    )

    fun formatLocalDateTime(
        value: LocalDateTime,
        pattern: String = DATE_TIME_PRESENTATION_VALUE_24_H,
        locale: Locale = Locale.getDefault()
    ): String = value.format(
        DateTimeFormatter.ofPattern(
            pattern,
            locale
        )
    )

    fun formatLocalDateTime(
        value: LocalDateTime,
        style: FormatStyle = FormatStyle.MEDIUM,
        locale: Locale = Locale.getDefault()
    ): String = value.format(
        DateTimeFormatter
            .ofLocalizedDate(style)
            .withLocale(locale)
    )

    fun formatLocalDate(value: LocalDate, style: FormatStyle, locale: Locale = Locale.getDefault()): String =
        value.format(
            DateTimeFormatter
                .ofLocalizedDate(style)
                .withLocale(locale)
        )

    fun timestampToLocalDateTime(timestamp: Long, timezone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), timezone)

    const val DATE_TIME_PRESENTATION_VALUE_24_H = "yyyy-MM-dd HH:mm:ss"
    const val DATE_TIME_PRESENTATION_VALUE_12_H = "yyyy-MM-dd HH:mm:ss a"
    const val DATE_PRESENTATION_VALUE = "dd/MM/yyyy"
    const val DATE_PLACEHOLDER = "—"
}