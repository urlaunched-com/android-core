package com.urlaunched.android.common.formatters

import java.text.NumberFormat

object DecimalFormatter {
    const val NO_FRACTION_DIGITS = 0

    private const val DASH = "—"
    private const val THOUSANDS = "k"
    private const val MILLIONS = "M"
    private const val EMPTY_STRING = ""
    private const val COMA = ","
    private const val DOT = "."
    private const val ONE_HUNDRED = 100
    private const val ONE_THOUSANDS = 1000
    private const val ONE_MILLION = 1000000
    private const val DEFAULT_FRACTION_DIGITS = 2

    fun Double?.formatNumber(
        minimumFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        maximumFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        postfix: String = EMPTY_STRING,
        useGrouping: Boolean = false
    ): String = if (this != null) {
        NumberFormat.getInstance().apply {
            this.maximumFractionDigits = maximumFractionDigits
            this.minimumFractionDigits = minimumFractionDigits
            this.isGroupingUsed = useGrouping
        }.format(this) + postfix
    } else {
        DASH
    }

    fun Int?.formatNumber(): String = NumberFormat.getInstance().format(this)

    fun Int?.toDollarCent(
        minimumFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        maximumFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        useGrouping: Boolean = false
    ): String = (this?.toDouble()?.div(ONE_HUNDRED)).formatNumber(
        minimumFractionDigits,
        maximumFractionDigits,
        useGrouping = useGrouping
    )

    fun Long?.toDollarCent(
        minimumFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        maximumFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        useGrouping: Boolean = false
    ): String = (this?.toDouble()?.div(ONE_HUNDRED)).formatNumber(
        minimumFractionDigits,
        maximumFractionDigits,
        useGrouping = useGrouping
    )

    fun Int?.toThousandsOrMillions(): String {
        val (number, postfix) = if (this != null) {
            if (this.toDouble() >= ONE_MILLION) {
                this.toDouble().div(ONE_MILLION) to MILLIONS
            } else {
                this.toDouble().div(ONE_THOUSANDS) to THOUSANDS
            }
        } else {
            null to EMPTY_STRING
        }
        return number.formatNumber(
            minimumFractionDigits = NO_FRACTION_DIGITS,
            maximumFractionDigits = 1,
            postfix = postfix
        )
    }

    fun Double.dollarsToCent() = (this * 100).toLong()

    fun Long.dollarsToCent() = this * 100

    fun Long.centsToDollar() = (this / 100.0)

    fun Float.toPercent() = (this * 100).toInt()

    fun Int.toPercent(): Float = this / 100f
}