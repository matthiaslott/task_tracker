package com.example.tasktracker.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class NavDataItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
)

val routes = listOf(
    NavDataItem("main", "Task Tracker", Icons.Rounded.Inbox),
    NavDataItem("archive", "Archive", Icons.Rounded.Archive),
    NavDataItem("settings", "Settings", Icons.Rounded.Settings),
)