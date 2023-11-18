@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasktracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource

import com.example.tasktracker.R
import com.example.tasktracker.ui.TaskViewModel
import com.example.tasktracker.ui.components.TaskDisplay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navigationCallback: () -> Unit,
    addTaskCallback: () -> Unit,
    modifyTaskCallback: (Int) -> Unit,
    taskViewModel: TaskViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = navigationCallback) {
                        Icon(Icons.Rounded.Menu, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = addTaskCallback,
                    ) {
                        Icon(Icons.Rounded.Add, null)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = hostState,
            )
        }
    ) {
        TaskDisplay(
            modifier = Modifier.padding(it),
            taskViewModel = taskViewModel,
            chipFilter = { !it.archived },
            baseTaskFilter = { !it.archived },
            taskFilter = { task, chip -> task.type == chip },
            swipeLeftIcon =  Icons.Rounded.Edit,
            swipeLeftColor = colorResource(R.color.yellow),
            onSwipeLeft =  {
                modifyTaskCallback(it.id)
                false
            },
            swipeRightIcon = Icons.Rounded.Archive,
            swipeRightColor = colorResource(R.color.red),
            onSwipeRight = {
                taskViewModel.archiveTask(it.id)
                coroutineScope.launch {
                    val result = hostState.showSnackbar(
                        message = "Task '${it.title}' was archived",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short,
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> { taskViewModel.unarchiveTask(it.id) }
                        SnackbarResult.Dismissed -> { }
                    }
                }
                false
            },
        )
    }
}