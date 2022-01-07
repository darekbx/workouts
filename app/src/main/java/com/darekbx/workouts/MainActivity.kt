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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.darekbx.workouts.ui.theme.WorkoutsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Hello Workouts")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WorkoutsTheme {
        Greeting("Android")
    }
}
