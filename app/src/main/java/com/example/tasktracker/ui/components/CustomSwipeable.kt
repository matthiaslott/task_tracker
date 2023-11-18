@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tasktracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwipeable(
    modifier: Modifier = Modifier,
    swipeThreshold: Float = 0.2F,
    vibrate: Boolean = true,
    swipeLeftIcon: ImageVector,
    swipeLeftColor: Color,
    onSwipeLeft: () -> Boolean,
    swipeRightIcon: ImageVector,
    swipeRightColor: Color,
    onSwipeRight: () -> Boolean,
    content: @Composable RowScope.() -> Unit,

    ) {
    val threshold = LocalDensity.current.run { LocalConfiguration.current.screenWidthDp.dp.toPx() * swipeThreshold }
    val hapticFeeback = LocalHapticFeedback.current
    val dismissState = rememberDismissState(
        positionalThreshold = { threshold },
        confirmValueChange = {
            when(it) {
                DismissValue.Default -> true
                DismissValue.DismissedToEnd -> onSwipeRight()
                DismissValue.DismissedToStart -> onSwipeLeft()
            }
        },
    )

    LaunchedEffect(dismissState.targetValue) {
        if (vibrate && dismissState.targetValue != dismissState.currentValue && dismissState.targetValue != DismissValue.Default) {
            hapticFeeback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        background = {

            val (color, alignment, icon) = when(dismissState.dismissDirection) {
                DismissDirection.StartToEnd -> Triple(swipeRightColor, Alignment.CenterStart, swipeRightIcon)
                DismissDirection.EndToStart -> Triple(swipeLeftColor, Alignment.CenterEnd, swipeLeftIcon)
                null -> Triple(Color.Unspecified, Alignment.Center, null)
            }

            Box(
                modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium).background(color),
                contentAlignment = alignment,
            ) {
                if (icon != null) {
                    Icon(icon, null,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        dismissContent = content
    )
}