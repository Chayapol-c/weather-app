package com.example.weatherapp.extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long.unixTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }.let {
        val date = Date(this * 1000L)
        it.format(date)
    }
}

fun String.getUnit(): String {
    return if ("US" == this || "LR" == this || "MM" == this) {
        "°F"
    } else {
        "°C"
    }
}
