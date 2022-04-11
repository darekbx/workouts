package com.darekbx.workouts.ui.workout

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.model.Workout
import com.darekbx.workouts.utils.FastForwardIncrease
import com.darekbx.workouts.utils.PlaybackSpeed
import com.darekbx.workouts.viewmodels.WorkoutsViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File

@Composable
fun WorkoutScreen(
    uid: String,
    workoutsViewModel: WorkoutsViewModel = hiltViewModel()
) {
    val workout = workoutsViewModel.workout(uid).observeAsState()
    var (playSpeedState, fastForwardState) = workoutsViewModel.loadPlaybackSettings()

    if (workout.value != null) {
        MainLayout(workout.value!!, playSpeedState, fastForwardState)
    } else {
        CommonProgress()
    }
}

@Composable
private fun MainLayout(
    workout: Workout,
    playSpeedState: PlaybackSpeed,
    fastForwardState: FastForwardIncrease
) {
    Column() {
        DisplayVideo(
            modifier = Modifier.fillMaxWidth(),
            movieUri = workout.moviePath
        )
    }
}

@Composable
private fun DisplayVideo(modifier: Modifier = Modifier, movieUri: String) {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(movieUri)
    val playWhenReady by rememberSaveable { mutableStateOf(true) }
    player.setMediaItem(mediaItem)
    playerView.player = player

    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady
    }

    AndroidView(
        modifier = modifier,
        factory = { playerView })
}

@Composable
private fun CommonProgress() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.33F)),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}
