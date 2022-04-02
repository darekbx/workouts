package com.darekbx.workouts.ui.workouts

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.workouts.R
import com.darekbx.workouts.model.Workout

@Composable
fun WorkoutsScreen(
    workouts: State<List<Workout>>
) {
    Workouts(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            workouts.value
    )
}

@Composable
fun Workouts(
    modifier: Modifier,
    workouts: List<Workout>
) {
    LazyColumn(modifier = modifier) {
        items(workouts) {
            WorkoutItem(Modifier.fillMaxWidth(), it)
        }
    }
}

@Preview
@Composable
fun WorkoutItem(
    modifier: Modifier = Modifier,
    workout: Workout = defaultWorkout.copy(
        thumbnail =  ImageBitmap.imageResource(id = R.drawable.preview_frame).asAndroidBitmap()
    )
) {
    Row(modifier) {
        Image(
            modifier = Modifier.width(60.dp),
            bitmap = workout.thumbnail.asImageBitmap(),
            contentDescription = workout.name
        )
        Text(text = "${workout.name}")
    }
}

private val defaultWorkout = Workout(
    "uid",
    "Workout 1",
    "",
    1648926866000,
    12,
    15 * 60 * 1000 + 12 * 1000,
    Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
)
