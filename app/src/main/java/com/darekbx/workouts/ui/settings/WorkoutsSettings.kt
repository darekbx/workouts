package com.darekbx.workouts.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.model.Workout
import com.darekbx.workouts.ui.workouts.LabelText
import com.darekbx.workouts.ui.workouts.defaultWorkout
import com.darekbx.workouts.utils.toFormattedDateTime
import com.darekbx.workouts.viewmodels.WorkoutsViewModel
import java.util.concurrent.TimeUnit

@ExperimentalComposeUiApi
@Composable
fun WorkoutsSettings(
    workoutsViewModel: WorkoutsViewModel = hiltViewModel(),
    onAdd: () -> Unit
) {
    val workouts = workoutsViewModel.workouts().observeAsState(listOf())
    ConstraintLayout {
        val (button) = createRefs()
        ListWorkounts(
            Modifier.fillMaxSize(),
            workouts = workouts.value,
            onDelete = { workoutsViewModel.delete(it) }
        )

        Button(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .constrainAs(button) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                },
            onClick = { onAdd() }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun ListWorkounts(
    modifier: Modifier = Modifier,
    workouts: List<Workout>,
    onDelete: (Workout) -> Unit = { }
) {
    LazyColumn(modifier) {
        items(workouts) { workout ->
            WorkoutItem(workout = workout, onDelete = onDelete)
        }
    }
}

@Preview(device = Devices.PIXEL_2_XL, widthDp = 500)
@Composable
fun WorkoutItem(
    modifier: Modifier = Modifier,
    workout: Workout = defaultWorkout(),
    onDelete: (Workout) -> Unit = { }
) {
    val lengthMinutes = TimeUnit.MILLISECONDS.toMinutes(workout.length)
    val lastPlayed = workout.lastPlayed
        .takeIf { it > 0 }
        ?.toFormattedDateTime()
        ?: "N/A"
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = "${workout.name}",
                style = MaterialTheme.typography.h5
            )
            LabelText(label = "Length: ", value = "${lengthMinutes} minutes")
            LabelText(label = "Last played: ", value = lastPlayed)
            LabelText(label = "Times played: ", value = "${workout.timesPlayed}")
        }
        Spacer(modifier = Modifier.weight(1f))
        /**
         * TODO: connect Delete and Edit buttons, improve UI
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            CardButton(
                Modifier
                    .size(56.dp, 56.dp)
                    .clickable { onDelete(workout) },
                Icons.Default.Delete
            )
            CardButton(
                Modifier
                    .size(56.dp, 56.dp)
                    .clickable { },
                Icons.Default.Edit
            )
        }*/
    }
}

@Composable
private fun CardButton(
    modifier: Modifier = Modifier,
    icon: ImageVector
) {
    Card(modifier.padding(top = 8.dp, end = 8.dp), elevation = 8.dp) {
        Icon(modifier = Modifier.padding(4.dp), imageVector = icon, contentDescription = "$icon")
    }
}