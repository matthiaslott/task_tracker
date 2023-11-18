@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasktracker.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.tasktracker.R
import com.example.tasktracker.ui.TaskViewModel
import com.example.tasktracker.ui.components.ImportDialog

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigationCallback: () -> Unit,
    taskViewModel: TaskViewModel
) {
    val context = LocalContext.current
    val importDialogVisible = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = navigationCallback) {
                        Icon(Icons.Rounded.Menu, null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = modifier.padding(it)
        ) {
            ImportDialog(
                taskViewModel = taskViewModel,
                dialogVisible = importDialogVisible,
            )
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    modifier = modifier,
                    onClick = { importDialogVisible.value = true }
                ) {
                    Text(stringResource(R.string.import_config))
                }
                OutlinedButton(
                    onClick = {
                        val textIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, taskViewModel.asJsonString())
                            type = "text/join"
                        }
                        val shareIntent = Intent.createChooser(textIntent, "Export Configuration")
                        context.startActivity(shareIntent)
                    }
                ) {
                    Text(stringResource(R.string.export_config))
                }
            }
            Text(stringResource(R.string.delete_hint),
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

