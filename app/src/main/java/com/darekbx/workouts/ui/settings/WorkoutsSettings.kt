package com.darekbx.workouts.ui.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
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
            Modifier
                .fillMaxSize()
                .padding(8.dp),
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
    val deleteConfirmation = remember { mutableStateOf(false) }
    val lengthMinutes = TimeUnit.MILLISECONDS.toMinutes(workout.length)
    val lastPlayed = workout.lastPlayed
        .takeIf { it > 0 }
        ?.toFormattedDateTime()
        ?: "N/A"
    Row(modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.h5
            )
            LabelText(label = "Length: ", value = "$lengthMinutes minutes")
            LabelText(label = "Last played: ", value = lastPlayed)
            LabelText(label = "Times played: ", value = "${workout.timesPlayed}")
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .width(80.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                "Delete",
                modifier =
                Modifier
                    .size(24.dp, 24.dp)
                    .clickable { deleteConfirmation.value = true },
            )
        }
    }

    if (deleteConfirmation.value) {
        DeleteWorkoutDialog(deleteConfirmation, onDelete, workout)
    }
}

@Composable
private fun DeleteWorkoutDialog(
    deleteConfirmation: MutableState<Boolean>,
    onDelete: (Workout) -> Unit,
    workout: Workout
) {
    AlertDialog(
        onDismissRequest = { deleteConfirmation.value = false },
        dismissButton = {
            Button(onClick = { deleteConfirmation.value = false }) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            Button(onClick = {
                onDelete(workout)
                deleteConfirmation.value = false
            }) {
                Text(text = "Delete")
            }
        },
        title = { Text(text = "Delete") },
        text = { Text(text = "Delete ${workout.name}?") }
    )
}
