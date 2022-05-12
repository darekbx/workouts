package com.darekbx.workouts.ui.workout

import android.content.Context
import android.media.TimedText
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.R
import com.darekbx.workouts.model.Workout
import com.darekbx.workouts.utils.FastForwardIncrease
import com.darekbx.workouts.utils.PlaybackSpeed
import com.darekbx.workouts.utils.toFormattedTime
import com.darekbx.workouts.viewmodels.WorkoutsViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun WorkoutScreen(
    uid: String,
    workoutsViewModel: WorkoutsViewModel = hiltViewModel()
) {
    val workout = workoutsViewModel.workout(uid).observeAsState()
    val (playSpeedState, fastForwardState) = workoutsViewModel.loadPlaybackSettings()

    LaunchedEffect(uid) {
        workoutsViewModel.markAsPlayed(uid)
    }

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
    DisplayVideo(
        modifier = Modifier.fillMaxHeight(),
        movieFile = workout.moviePath
    )
}

@Composable
private fun DisplayVideo(modifier: Modifier = Modifier, movieFile: String) {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(computeMovieUrl(context, movieFile))
    val playWhenReady by rememberSaveable { mutableStateOf(true) }
    player.setMediaItem(mediaItem)

    playerView.player = player
    playerView.hideController()
    playerView.controllerAutoShow = false

    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady
    }

    Column(modifier) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { playerView })
        TimeText(
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp),
            player = player
        )
        DisplayControls(
            Modifier
                .weight(1F)
                .padding(32.dp)
                .fillMaxWidth(),
            onPlayingChanged = { state ->
                if (state) player.playWhenReady = false
                else player.playWhenReady = true
            })
    }
}

@Composable
private fun TimeText(modifier: Modifier, player: Player) {
    var currentValue by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            if (player.currentPosition > 0) {
                currentValue = player.duration - player.currentPosition
            }
            delay(1000)
        }
    }
    Row(modifier = modifier) {
        Text(
            text = currentValue.toFormattedTime(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = "left",
            fontSize = 16.sp
        )
    }
}

@Composable
private fun DisplayControls(
    modifier: Modifier = Modifier,
    onPreviousClick: () -> Unit = { },
    onNextClick: () -> Unit = { },
    onPlayingChanged: (Boolean) -> Unit
) {
    val buttonColor = ButtonDefaults.buttonColors(
        backgroundColor = Color.White.copy(alpha = 0.15F),
        contentColor = Color.White
    )
    var playPauseIcon by remember { mutableStateOf(true) }

    Column(modifier, verticalArrangement = Arrangement.Center) {
        Row(Modifier.height(72.dp)) {
            Button(
                modifier = Modifier.fillMaxHeight(),
                colors = buttonColor,
                onClick = { onPreviousClick() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "back"
                )
            }
            Button(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .padding(start = 4.dp, end = 4.dp),
                colors = buttonColor,
                onClick = {
                    onPlayingChanged(playPauseIcon)
                    playPauseIcon = !playPauseIcon
                }) {
                Icon(
                    painterResource(id = if (playPauseIcon) R.drawable.ic_pause else R.drawable.ic_play),
                    contentDescription = "play/pause"
                )
            }
            Button(
                modifier = Modifier.fillMaxHeight(),
                colors = buttonColor,
                onClick = { onNextClick() }
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "forward"
                )
            }
        }
    }
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

@Composable
private fun computeMovieUrl(
    context: Context,
    movieFile: String
) = File(context.filesDir, movieFile).toUri()
/*
@Preview
@Composable
private fun ControlsPreview() {
    DisplayControls(
        Modifier
            .fillMaxWidth()
            .height(72.dp))
}*/