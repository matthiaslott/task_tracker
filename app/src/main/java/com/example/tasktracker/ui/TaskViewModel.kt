package com.example.tasktracker.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tasktracker.TAG
import com.example.tasktracker.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

class TaskViewModel(
    filesDir: File
) : ViewModel() {
    // Ui State
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    // Serialisation
    private lateinit var file: File

    fun isValidJsonString(str: String) : Boolean {
        try {
            val temp = Json.decodeFromString<TaskUiState>(str)
            return true
        }
        catch (e : Exception) {
            return false
        }
    }

    fun loadFromJsonString(str: String) {
        _uiState.update { _ ->
            Json.decodeFromString<TaskUiState>(str)
        }
        store()
    }

    init {
        load(filesDir)
    }

    fun load(filesDir : File) {
        // open the file
        Log.d(TAG, "[TaskViewModel] Loading")
        file = File(filesDir, "data.json")
        if (file.exists()) {
            Log.d(TAG, "[TaskViewModel] File exists")
            _uiState.update { _ ->
                Json.decodeFromString<TaskUiState>(file.readText())
            }
        }
    }

    fun asJsonString() : String {
        return Json.encodeToString<TaskUiState>(_uiState.value)
    }

    private fun store() {
        Log.d(TAG, "[TaskViewModel] Storing")
        file.writeText(asJsonString())
    }

    fun addTask(
        title: String,
        description: String,
        date: LocalDate,
        type: String,
    ) {
        // Note: having ints as task ids is way more than enough
        var taskId = 0;
        if (_uiState.value.tasks.isNotEmpty()) {
            taskId = _uiState.value.tasks.keys.max() + 1
        }
        Log.d(TAG, "[TaskViewModel] Adding task $taskId")
        _uiState.update { currentState ->
            val newData = currentState.tasks.toMutableMap()
            newData[taskId] = Task(taskId, title, description, date, false, type)
            TaskUiState(newData)
        }
        store()
    }

    fun modifyTask(
        taskId: Int,
        title: String,
        description: String,
        date: LocalDate,
        type: String,
    ) {
        assert(_uiState.value.tasks.containsKey(taskId))
        Log.d(TAG, "[TaskViewModel] Modifying task $taskId")
        _uiState.update { currentState ->
            val newData = currentState.tasks.toMutableMap()
            newData[taskId] = newData[taskId]!!.copy(
                title = title,
                description = description,
                date = date,
                type = type,
            )
            TaskUiState(newData)
        }
        store()
    }

    fun archiveTask(taskId: Int) {
        assert(_uiState.value.tasks.containsKey(taskId))
        Log.d(TAG, "[TaskViewModel] Archiving task $taskId")
        _uiState.update { currentState ->
            val newData = currentState.tasks.toMutableMap()
            newData[taskId] = newData[taskId]!!.copy(
                archived = true
            )
            TaskUiState(newData)
        }
        store()
    }

    fun unarchiveTask(taskId: Int) {
        assert(_uiState.value.tasks.containsKey(taskId))
        Log.d(TAG, "[TaskViewModel] Unarchiving task $taskId")
        _uiState.update { currentState ->
            val newData = currentState.tasks.toMutableMap()
            newData[taskId] = newData[taskId]!!.copy(
                archived = false
            )
            TaskUiState(newData)
        }
        store()
    }

    fun deleteTask(taskId: Int) {
        assert(_uiState.value.tasks.containsKey(taskId))
        Log.d(TAG, "[TaskViewModel] Deleting task $taskId")
        _uiState.update { currentState ->
            val newData = currentState.tasks.toMutableMap()
            newData.remove(taskId)
            TaskUiState(newData)
        }
        store()
    }

    fun undeleteTask(task: Task) {
        assert(!_uiState.value.tasks.containsKey(task.id))
        Log.d(TAG, "[TaskViewModel] Undeleting task ${task.id}")
        _uiState.update { currentState ->
            val newData = currentState.tasks.toMutableMap()
            newData[task.id] = task
            TaskUiState(newData)
        }
        store()
    }
}