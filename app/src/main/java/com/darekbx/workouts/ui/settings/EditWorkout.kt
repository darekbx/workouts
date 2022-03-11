package com.darekbx.workouts.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.workouts.R

@Preview
@Composable
fun EditWorkout(workoutUid: String? = null) {
    Column(Modifier.padding(all = 8.dp)) {
        WorkoutName(Modifier.fillMaxWidth())
        MovieSelectButton(Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
        )
    }
}

@Composable
fun WorkoutName(modifier: Modifier = Modifier, name: String? = null) {
    val text by remember { mutableStateOf(name ?: "") }
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = { },
        label = { Text(stringResource(id = R.string.movie_name_hint)) }
    )
}

@Composable
fun MovieSelectButton(modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    Button(
        modifier = modifier,
        onClick = { /* ... */ },
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 12.dp
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_movie),
            contentDescription = "Favorite",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(id = R.string.select_movie))
    }
}