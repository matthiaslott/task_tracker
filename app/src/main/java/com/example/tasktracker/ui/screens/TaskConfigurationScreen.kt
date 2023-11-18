@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.tasktracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tasktracker.R
import com.example.tasktracker.ui.TaskViewModel
import com.example.tasktracker.ui.components.ChipBar
import com.example.tasktracker.ui.components.CustomDatePicker
import com.example.tasktracker.ui.getChips
import com.example.tasktracker.ui.getTask
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TaskConfigurationScreen(
    modifier: Modifier = Modifier,
    navigationCallback: () -> Unit,
    taskViewModel: TaskViewModel,
    taskId: Int?
) {
    val taskUiState = taskViewModel.uiState.collectAsState()
    // TypeSelection
    val maxChipLength = 12
    val selectedChip = rememberSaveable { mutableStateOf(
        if (taskId != null)
            taskUiState.value.getTask(taskId).type
        else
            ""
    )}
    // DateSelection
    val selectedDate = rememberSaveable { mutableStateOf(
        if (taskId != null)
            taskUiState.value.getTask(taskId).date
        else
            LocalDate.now()
    )}
    // TitleSelection
    val maxTitleLength = 25
    val selectedTitle = rememberSaveable { mutableStateOf(
        if (taskId != null)
            taskUiState.value.getTask(taskId).title
        else
            ""
    ) }
    // DescriptionSelection
    val maxDescriptionLength = 25
    val selectedDescription = rememberSaveable { mutableStateOf(
        if (taskId != null)
            taskUiState.value.getTask(taskId).description
        else
            ""
    ) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (taskId == null) R.string.add_task else R.string.modify_task)) },
                navigationIcon = {
                    IconButton(onClick = navigationCallback) {
                        Icon(Icons.Rounded.Close, null)
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            ChipBar(
              chips = taskUiState.value.getChips { !it.archived },
              selectedChip = selectedChip,
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1F),
                    value = selectedChip.value,
                    onValueChange = { selectedChip.value = if (it.length <= maxChipLength) it else it.substring(0, maxChipLength) },
                    label = { Text(stringResource(R.string.task_type)) },
                    supportingText = { Text(stringResource(R.string.char_limit, maxChipLength)) },
                    isError = selectedChip.value.length > maxChipLength,
                    singleLine = true,
                )
                Spacer(
                    modifier = Modifier.width(12.dp)
                )
                CustomDatePicker(
                    modifier = Modifier.weight(1F),
                    selectedDate = selectedDate
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                value = selectedTitle.value,
                onValueChange = { selectedTitle.value = if (it.length <= maxTitleLength) it else it.substring(0, maxTitleLength) },
                label = { Text(stringResource(R.string.task_title)) },
                supportingText = { Text(stringResource(R.string.char_limit, maxTitleLength)) },
                isError = selectedTitle.value.length > maxTitleLength,
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                value = selectedDescription.value,
                onValueChange = { selectedDescription.value = if (it.length <= maxDescriptionLength) it else it.substring(0, maxDescriptionLength) },
                label = { Text(stringResource(R.string.task_description)) },
                supportingText = { Text(stringResource(R.string.char_limit, maxDescriptionLength)) },
                isError = selectedDescription.value.length > maxDescriptionLength,
                singleLine = true,
            )
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                OutlinedButton(
                    onClick = navigationCallback,
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(
                    modifier = Modifier.width(12.dp),
                )
                FilledTonalButton(
                    onClick = {
                        if (taskId == null) {
                            taskViewModel.addTask(
                                title = selectedTitle.value,
                                description = selectedDescription.value,
                                date = selectedDate.value,
                                type = selectedChip.value,
                            )
                        }
                        else {
                            taskViewModel.modifyTask(
                                taskId = taskId,
                                title = selectedTitle.value,
                                description = selectedDescription.value,
                                date = selectedDate.value,
                                type = selectedChip.value,
                            )
                        }
                        navigationCallback()
                    },
                ) {
                    Text(stringResource(if (taskId == null) R.string.create_task else R.string.save_task))
                }
            }
        }
    }
}

