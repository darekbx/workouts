package com.darekbx.workouts.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.ui.theme.Typography
import com.darekbx.workouts.utils.FastForwardIncrease
import com.darekbx.workouts.utils.PlaybackSpeed
import com.darekbx.workouts.viewmodels.WorkoutsViewModel

@Composable
fun PlaybackSettings(
    workoutsViewModel: WorkoutsViewModel = hiltViewModel(),
) {
    var (playSpeedState, fastForwardState) = workoutsViewModel.loadPlaybackSettings()
    var playSpeed by remember { mutableStateOf(playSpeedState) }
    var fastForward by remember { mutableStateOf(fastForwardState) }

    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Playback Settings",
            style = Typography.body1,
        )
        PlaySpeed(value = playSpeed) {
            playSpeed = it
            workoutsViewModel.persistPlaybackSettings(it, fastForward)
        }
        FastForwardInterval(value = fastForward) {
            fastForward = it
            workoutsViewModel.persistPlaybackSettings(playSpeed, it)
        }
    }
}

@Composable
fun PlaySpeed(
    modifier: Modifier = Modifier,
    value: PlaybackSpeed = PlaybackSpeed.SPEED_1_0,
    onChanged: (PlaybackSpeed) -> Unit = { }
) {
    Column(modifier.padding(16.dp)) {
        Text(
            text = "Play speed",
            style = Typography.h5,
            color = Color.Gray
        )
        Row(Modifier.padding(top = 8.dp)) {
            for (item in PlaybackSpeed.values()){
                ActionButton(
                    text = item.label,
                    checked = value == item
                ) { onChanged(item) }
            }
        }
    }
}

@Composable
fun FastForwardInterval(
    modifier: Modifier = Modifier,
    value: FastForwardIncrease = FastForwardIncrease.FF_10,
    onChanged: (FastForwardIncrease) -> Unit = { }
) {
    Column(modifier.padding(16.dp)) {
        Text(
            text = "Fast forward interval",
            style = Typography.h5,
            color = Color.Gray
        )
        Row(Modifier.padding(top = 8.dp)) {
            for (item in FastForwardIncrease.values()) {
                ActionButton(
                    text = item.label,
                    checked = value == item
                ) { onChanged(item) }
            }
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(end = 4.dp)
            .alpha(if (checked) 1.0F else 0.4F),
        onClick = { onClick() },
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
fun PlaybackSettingsPreview() {
    PlaybackSettings()
}

@Preview
@Composable
fun EnabledActionButton() {
    ActionButton(text = "Enabled", checked = true) { }
}

@Preview
@Composable
fun DisabledActionButton() {
    ActionButton(text = "Disabled", checked = false) { }
}