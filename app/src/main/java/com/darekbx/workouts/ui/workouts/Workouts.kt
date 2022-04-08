package com.darekbx.workouts.ui.workouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.workouts.R
import com.darekbx.workouts.model.Workout
import com.darekbx.workouts.utils.toFormattedDateTime
import java.util.concurrent.TimeUnit

@Composable
fun WorkoutsScreen(
    workouts: State<List<Workout>>,
    onWorkoutClick: (Workout) -> Unit
) {
    Workouts(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            workouts.value
    ) { onWorkoutClick(it) }
}

@Composable
fun Workouts(
    modifier: Modifier,
    workouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit = { }
) {
    LazyColumn(modifier = modifier) {
        items(workouts) {
            WorkoutItem(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { onWorkoutClick(it) },
                it//.copy(thumbnail = ImageBitmap.imageResource(id = R.drawable.preview_frame).asAndroidBitmap())
            )
        }
    }
}

@Preview(device = Devices.PIXEL_2_XL)
@Composable
fun WorkoutItem(
    modifier: Modifier = Modifier,
    workout: Workout = defaultWorkout()
) {
    val lengthMinutes = TimeUnit.MILLISECONDS.toMinutes(workout.length)
    val lastPlayed = workout.lastPlayed
        .takeIf { it > 0 }
        ?.toFormattedDateTime()
        ?: "N/A"
    Row(modifier) {
        Image(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .width(140.dp),
            bitmap = workout.thumbnail.asImageBitmap(),
            contentDescription = workout.name,
        )
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = "${workout.name}",
                style = MaterialTheme.typography.h5
            )
            LabelText(label = "Length: ", value = "${lengthMinutes} minutes")
            LabelText(label = "Last played: ", value = lastPlayed)
            LabelText(label = "Times played: ", value = "${workout.timesPlayed}")
        }
    }
}

@Composable
fun LabelText(modifier: Modifier = Modifier, label: String, value: String) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.width(80.dp),
            text = label,
            style = MaterialTheme.typography.h6,
            color = Color.LightGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
fun defaultWorkout() = Workout(
    "uid",
    "Workout 1",
    "",
    1648926866000L,
    12,
    15 * 60 * 1000 + 12 * 1000,
    ImageBitmap.imageResource(id = R.drawable.preview_frame).asAndroidBitmap()
)
