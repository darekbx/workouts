package com.darekbx.workouts.utils

import java.sql.Date
import java.text.SimpleDateFormat

fun Long.toFormattedDateTime(): String {
    return SimpleDateFormat("yyyy-MM-dd").format(Date(this))
}

fun String.toSeconds(): Long {
    val chunks = split(":").map { it.toInt() }
    return when (chunks.size) {
        3 -> /*H*/(chunks[0] * 60L * 60L) + /*M*/(chunks[1] * 60L) + /*S*/chunks[2]
        2 -> /*M*/(chunks[0] * 60L) + /*S*/chunks[1]
        else -> /*S*/chunks[1].toLong()
    }
}
