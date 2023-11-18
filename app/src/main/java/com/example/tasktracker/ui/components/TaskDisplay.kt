@file:OptIn(ExperimentalFoundationApi::class)

package com.example.tasktracker.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.tasktracker.data.Task
import com.example.tasktracker.data.dateToString
import com.example.tasktracker.ui.*

@Composable
fun TaskDisplay(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel,
    // Filtering
    chipFilter: (Task) -> Boolean, // selects which chips to display
    baseTaskFilter: (Task) -> Boolean, // selects which tasks should be displayed
    taskFilter: (Task, String) -> Boolean, // filtering based on selected chip
    // Swiping
    swipeLeftIcon: ImageVector,
    swipeLeftColor: Color,
    onSwipeLeft: (Task) -> Boolean,
    swipeRightIcon: ImageVector,
    swipeRightColor: Color,
    onSwipeRight: (Task) -> Boolean,
) {
    val selectedChip = rememberSaveable { mutableStateOf("") }

    val taskUiState = taskViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
    ) {
        stickyHeader {
            ChipBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                chips = taskUiState.value.getChips(chipFilter),
                selectedChip = selectedChip,
            )
            Divider(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp)
            )
        }
        taskUiState.value.getTasks(baseTaskFilter).keys.forEach { localDate ->
            val tasks = taskUiState.value.getTasksForDate({task -> baseTaskFilter(task) && (selectedChip.value == "" || taskFilter(task, selectedChip.value)) }, localDate)
            item(key = localDate.toString() + tasks.size) {
                Box(
                    modifier = Modifier.animateContentSize().animateItemPlacement()
                ) {
                    if (tasks.isNotEmpty()) {
                        ElevatedCard(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                            ) {
                                Text(dateToString(localDate), style = MaterialTheme.typography.titleLarge)
                                Divider(
                                    modifier = Modifier.padding(6.dp)
                                )
                                tasks.forEach { task ->
                                    CustomSwipeable(
                                        swipeLeftIcon = swipeLeftIcon,
                                        swipeLeftColor = swipeLeftColor,
                                        onSwipeLeft = { onSwipeLeft(task) },
                                        swipeRightIcon = swipeRightIcon ,
                                        swipeRightColor = swipeRightColor,
                                        onSwipeRight = { onSwipeRight(task) }
                                    ) {
                                        TaskItem(
                                            task = task,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
) {
    ListItem(
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        headlineContent = { Text(task.title) },
        supportingContent = { if (task.description != "") Text(task.description)},
        trailingContent = {
            if (task.type != "") {
                SuggestionChip(
                    onClick = { },
                    label = { Text(task.type) }
                )
            }
        },
    )
}