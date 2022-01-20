package com.darekbx.workouts

/**
 * Settings:
 *  - fast forward inverval e.g. +/-10s (10s, 20s, 30s, ...)
 *  - play speed increase e.g. +/-5% (5%, 10%, 20%, ...)
 *  - movies:
 *    - add new movie
 *    - select name
 *    - choose frame for preview
 *    - add time markers for training steps*
 *
 * First screen:
 *  - list of the trainings:
 *    - 'back'
 *    - 'neck'
 *    - 'overall'?
 *  - each item:
 *    - when last played (e.g. 2d ago)
 *    - how many times played
 *    - image (selected one frame of the movie)
 *
 * Training screen:
 *  - movie playback
 *  - progress bar with time
 *  - pause button
 *  - -10s, +10s buttons (configurable in settings)
 *  - play speed buttons e.g. -5%, +5% (configurable in settings)
 *  - play speed is displayed
 *  - training step skip/redo*
 *
 *  * - optional
 */

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.darekbx.workouts.data.WorkoutsDao
import com.darekbx.workouts.data.dto.Marker
import com.darekbx.workouts.ui.theme.WorkoutsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var workoutsDao: WorkoutsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    WorkoutsScreen()
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

            Log.v("--------------", "Add")
            val a = workoutsDao.addMarker(Marker(UUID.randomUUID().toString(), "x", System.currentTimeMillis(), 1))
                Log.v("--------------", "Add result: $a")

            Log.v("--------------", "List markers")
            val m = workoutsDao.markers()
                Log.v("--------------", "List markers, count: ${m.size}")

        }
    }
}

@Composable
fun WorkoutsScreen() {
    Column() {
        Workouts(modifier = Modifier.fillMaxWidth().weight(1F))
        WorkoutsMenu(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun Workouts(modifier: Modifier) {
    Column(modifier = modifier) {
        (0 until 10).forEach {
            Text(text = "$it")
        }
    }
}

@Composable
fun WorkoutsMenu(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WorkoutsTheme {
        WorkoutsScreen()
    }
}
