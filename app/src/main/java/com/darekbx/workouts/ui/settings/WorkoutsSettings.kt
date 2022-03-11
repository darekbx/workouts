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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.data.dto.Workout
import com.darekbx.workouts.viewmodels.WorkoutsViewModel

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
            workouts = workouts.value
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
fun ListWorkounts(modifier: Modifier = Modifier, workouts: List<Workout>) {
    LazyColumn(modifier) {
        items(workouts) { workout ->
            WorkoutItem(workout = workout)
        }
    }
}

@Composable
private fun WorkoutItem(modifier: Modifier = Modifier, workout: Workout) {
    Row(modifier.fillMaxWidth()) {

        Text(text = "${workout.name}")
        Text(text = "${workout.length}")
    }
}
