package com.example.tasktracker.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun dateToString(date: LocalDate) : String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy (EEEE)")
    return date.format(formatter)
}