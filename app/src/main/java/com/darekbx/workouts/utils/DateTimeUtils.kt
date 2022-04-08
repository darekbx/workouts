package com.darekbx.workouts.utils

import java.sql.Date
import java.text.SimpleDateFormat

fun Long.toFormattedDateTime(): String {
    return SimpleDateFormat("yyyy-MM-dd").format(Date(this))
}