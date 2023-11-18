package com.example.tasktracker.ui

import com.example.tasktracker.data.Task
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*

@Serializable
data class TaskUiState(
    val tasks: Map<Int, Task> = mapOf()
)

// Extension function to obtain chip names
fun TaskUiState.getChips(chipFilter: (Task) -> Boolean) : List<String> {
    return tasks.values.filter(chipFilter).map{ it.type }.filter { it != "" }.distinct()
}

// Extension function to obtain the task corresponding to a given id
fun TaskUiState.getTask(id: Int) : Task {
    assert(tasks.containsKey(id))
    return tasks[id]!!
}

// Extension function to obtain a collection of dates and task-lists
fun TaskUiState.getTasks(taskFilter: (Task) -> Boolean) : SortedMap<LocalDate, List<Task>> {
    return tasks.values.filter(taskFilter).groupBy { it.date }.toSortedMap()
}

// Extension function to obtain the task-list for a specific date
fun TaskUiState.getTasksForDate(taskFilter: (Task) -> Boolean, localDate: LocalDate) : List<Task> {
    return this.getTasks(taskFilter)[localDate] ?: emptyList()
}