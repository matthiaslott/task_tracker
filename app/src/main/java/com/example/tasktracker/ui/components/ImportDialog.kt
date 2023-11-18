@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasktracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasktracker.R
import com.example.tasktracker.ui.TaskViewModel

@Composable
fun ImportDialog(
    taskViewModel: TaskViewModel,
    dialogVisible: MutableState<Boolean>,
) {
    val textFieldContent = rememberSaveable { mutableStateOf("") }
    val confirmCountdown = rememberSaveable { mutableStateOf(3) }

    val isValidContent = { taskViewModel.isValidJsonString(textFieldContent.value) }
    val closeDialog = {
        dialogVisible.value = false
        textFieldContent.value = "" // clear content of the TextField
        confirmCountdown.value = 3
    }
    AnimatedVisibility(visible = dialogVisible.value) {
        AlertDialog(onDismissRequest = { dialogVisible.value = false }) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.import_config),
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    TextField(
                        value = textFieldContent.value,
                        onValueChange = { textFieldContent.value = it },
                        label = { Text(stringResource(R.string.config)) },
                        supportingText = {
                            if (isValidContent())
                                Text(
                                    stringResource(R.string.click_count, stringResource(R.string.load), confirmCountdown.value),
                                    color = Color.Red,
                                )
                            else
                                Text(stringResource(R.string.invalid_json))
                        },
                        isError = !isValidContent(),
                        minLines = 5,
                        maxLines = 5,
                    )
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = closeDialog
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )
                        TextButton(
                            onClick = {
                                confirmCountdown.value--
                                if (confirmCountdown.value == 0) {
                                    // store
                                    taskViewModel.loadFromJsonString(textFieldContent.value)
                                    closeDialog()
                                }
                            },
                            enabled = isValidContent()
                        ) {
                            Text(stringResource(R.string.load))
                        }
                    }
                }
            }
        }
    }
}
