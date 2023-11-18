@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasktracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.tasktracker.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    selectedDate: MutableState<LocalDate> = rememberSaveable { mutableStateOf(LocalDate.now()) }
) {
    val openDialog = rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val longToLocalDate = { it: Long ->
        val instant = Instant.ofEpochMilli(it)
        val zonedDate = instant.atZone(ZoneId.systemDefault())
        zonedDate.toLocalDate()
    }
    val localDateToString = { it: LocalDate ->
        val formatter = DateTimeFormatter.ofPattern("dd. MMM yyyy")
        it.format(formatter)
    }

    OutlinedTextField(
        modifier = modifier,
        value = localDateToString(selectedDate.value),
        onValueChange = { },
        readOnly = true,
        label = { Text(stringResource(R.string.task_date)) },
        trailingIcon = {
            IconButton(onClick = { openDialog.value = true }) {
                Icon(Icons.Rounded.CalendarMonth, null)
            }
        },
        singleLine = true,
    )
    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = { openDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        if (datePickerState.selectedDateMillis != null) {
                            selectedDate.value = longToLocalDate(datePickerState.selectedDateMillis!!)
                        }
                    },
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = { !longToLocalDate(it).isBefore(LocalDate.now())}
            )
        }
    }
}