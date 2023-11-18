@file:OptIn(ExperimentalAnimationApi::class)

package com.example.tasktracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tasktracker.data.routes
import com.example.tasktracker.ui.TaskViewModel
import com.example.tasktracker.ui.screens.TaskConfigurationScreen
import com.example.tasktracker.ui.screens.ArchiveScreen
import com.example.tasktracker.ui.screens.MainScreen
import com.example.tasktracker.ui.screens.SettingsScreen
import com.example.tasktracker.ui.theme.TaskTrackerTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskTrackerTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    App(
                        taskViewModel = TaskViewModel(LocalContext.current.filesDir)
                    )
                }
            }
        }
    }
}

// Logging
val TAG = "TaskTracker"

@Composable
fun App(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberAnimatedNavController()

    // close drawer on back button press
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(R.string.app_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayMedium,
                )
                Divider(
                    modifier = Modifier.padding(all = 12.dp),
                )
                routes.forEach { route ->
                    NavigationDrawerItem(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        label = { Text(route.name) },
                        selected = route.id == navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route,
                        onClick = {
                            navController.navigate(route.id) {
                                launchSingleTop=true
                                popUpTo(routes[0].id) { inclusive = false}
                            }
                            coroutineScope.launch { drawerState.close() }
                        },
                        icon = { Icon(route.icon, null) },
                    )
                }
            }
        }
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = routes[0].id
        ) {
            composable(routes[0].id) {
                MainScreen(
                    navigationCallback = { coroutineScope.launch { drawerState.open() } },
                    addTaskCallback = {
                        Log.d(TAG, "[Navigation] Creating new task")
                        navController.navigate("newtask")
                    },
                    modifyTaskCallback = {
                        Log.d(TAG, "[Navigation] Modifying task ${it}")
                        navController.navigate("task/${it}")
                    },
                    taskViewModel = taskViewModel,
                )
            }
            composable(routes[1].id) {
                ArchiveScreen(
                    navigationCallback = { coroutineScope.launch { drawerState.open() } },
                    taskViewModel = taskViewModel,
                )
            }
            composable(routes[2].id) {
                SettingsScreen(
                    navigationCallback = { coroutineScope.launch { drawerState.open() } },
                    taskViewModel = taskViewModel,
                )
            }
            composable("task/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType }),
                enterTransition = { slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Up)},
                exitTransition = { slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Down)},
            ) {
                TaskConfigurationScreen(
                    navigationCallback = { navController.popBackStack() },
                    taskViewModel = taskViewModel,
                    taskId = it.arguments?.getInt("id"),

                )
            }
            composable("newtask",
                enterTransition = { slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Up)},
                exitTransition = { slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Down)},
            ) {
                TaskConfigurationScreen(
                    navigationCallback = { navController.popBackStack() },
                    taskViewModel = taskViewModel,
                    taskId = null,
                )
            }
        }
    }
}



//--------------------------------------------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskTrackerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            App(
                taskViewModel = TaskViewModel(LocalContext.current.filesDir)
            )
        }
    }
}
// Connect to Phone wirelessly:
// cd ../../Programs/AndroidSDK/platform-tools
// adb connect 192.168.178.25:<port>

/* Improvement Ideas:
 * - TODO: More sophisticated selection transitions
 * */

/* Custom Colors:
 * - Orange: (80, 150, 80)
 * - Yellow: (100, 150, 80)
 * - Red: (20, 150, 60)
 * - Green: (140, 150, 70)
 */