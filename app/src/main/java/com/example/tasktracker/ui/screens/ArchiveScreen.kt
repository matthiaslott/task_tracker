@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.tasktracker.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource

import com.example.tasktracker.R
import com.example.tasktracker.ui.TaskViewModel
import com.example.tasktracker.ui.components.TaskDisplay
import kotlinx.coroutines.launch

@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
    navigationCallback: () -> Unit,
    taskViewModel: TaskViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.archive)) },
                navigationIcon = {
                    IconButton(onClick = navigationCallback) {
                        Icon(Icons.Rounded.Menu, null)
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
            chipFilter = { true },
            baseTaskFilter = { it.archived },
            taskFilter = { task, chip -> task.type == chip },
            swipeLeftIcon =  Icons.Rounded.Unarchive,
            swipeLeftColor = colorResource(R.color.green),
            onSwipeLeft =  {
                taskViewModel.unarchiveTask(it.id)
                false
            },
            swipeRightIcon = Icons.Rounded.DeleteForever,
            swipeRightColor = colorResource(R.color.red),
            onSwipeRight = {
                taskViewModel.deleteTask(it.id)
                coroutineScope.launch {
                    val result = hostState.showSnackbar(
                        message = "Task '${it.title}' was deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short,
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> { taskViewModel.undeleteTask(it) }
                        SnackbarResult.Dismissed -> { }
                    }
                }
                false
            },
        )
    }
}