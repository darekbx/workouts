package com.darekbx.workouts.ui.workouts

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
            WorkoutItem(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp), it.copy(thumbnail = ImageBitmap.imageResource(id = R.drawable.preview_frame).asAndroidBitmap()))
        }
    }
}

@Preview
@Composable
fun WorkoutItem(
    modifier: Modifier = Modifier,
    workout: Workout = defaultWorkout.copy(
        thumbnail = ImageBitmap.imageResource(id = R.drawable.preview_frame).asAndroidBitmap()
    )
) {
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
            LabelText(label = "Last played: ", value = "${workout.lastPlayed}")
            LabelText(label = "Times played: ", value = "${workout.timesPlayed}")
            LabelText(label = "Length: ", value = "${workout.length / 1000}s")
        }
    }
}

@Composable
private fun LabelText(modifier: Modifier = Modifier, label: String, value: String) {
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

private val defaultWorkout = Workout(
    "uid",
    "Workout 1",
    "",
    1648926866000,
    12,
    15 * 60 * 1000 + 12 * 1000,
    Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
)
