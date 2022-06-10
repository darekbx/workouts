package com.darekbx.workouts.ui.workout

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.R
import com.darekbx.workouts.model.Marker
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
    val markers = workoutsViewModel.workoutMarkers(uid).observeAsState()
    val (playSpeedState, fastForwardState) = workoutsViewModel.loadPlaybackSettings()

    LaunchedEffect(uid) {
        workoutsViewModel.markAsPlayed(uid)
    }

    if (workout.value != null) {
        MainLayout(workout.value!!, markers.value!!, playSpeedState, fastForwardState)
    } else {
        CommonProgress()
    }
}

@Composable
private fun MainLayout(
    workout: Workout,
    markers: List<Marker>,
    playSpeedState: PlaybackSpeed,
    fastForwardState: FastForwardIncrease
) {
    DisplayVideo(
        modifier = Modifier.fillMaxHeight(),
        movieFile = workout.moviePath,
        markers,
        playSpeedState,
        fastForwardState
    )
}

@Composable
private fun DisplayVideo(
    modifier: Modifier = Modifier,
    movieFile: String,
    markers: List<Marker>,
    playSpeedState: PlaybackSpeed,
    fastForwardState: FastForwardIncrease
) {
    var markerIndex by remember { mutableStateOf(0) }
    val playWhenReadyState by rememberSaveable { mutableStateOf(true) }

    val context = LocalContext.current
    val exoPlayer = remember(context) {
        SimpleExoPlayer.Builder(context).build().apply {
            setPlaybackSpeed(playSpeedState.speed)
            prepare()
        }
    }

    Column(modifier) {
        DisposableEffect(AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(0.dp, 400.dp),
            factory = {
                PlayerView(context).apply {
                    val mediaItem = MediaItem.fromUri(computeMovieUrl(context, movieFile))
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = playWhenReadyState

                    player = exoPlayer
                    hideController()
                    controllerAutoShow = false
                }
            })) {
            onDispose { exoPlayer.release() }
        }
        TimeText(
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp, end = 8.dp),
            player = exoPlayer
        )
        PlaybackSettings(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 8.dp),
            playSpeedState,
            fastForwardState
        )
        DisplayControls(
            Modifier
                .weight(1F)
                .padding(16.dp)
                .fillMaxWidth(),
            onPlayingChanged = { state: Boolean ->
                exoPlayer.playWhenReady = !state
            },
            onNextClick = {
                val marker = markers[markerIndex]
                if (markerIndex < markers.size - 1) {
                    markerIndex++
                }
                exoPlayer.seekTo(marker.time)
            },
            onPreviousClick = {
                val marker = markers[markerIndex]
                if (markerIndex > 0) {
                    markerIndex--
                }
                exoPlayer.seekTo(marker.time)
            },
            onFastRewindClick = {
                exoPlayer.seekTo(exoPlayer.currentPosition - fastForwardState.value)
            },
            onFastForwardClick = {
                exoPlayer.seekTo(exoPlayer.currentPosition + fastForwardState.value)
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
private fun PlaybackSettings(
    modifier: Modifier,
    playSpeedState: PlaybackSpeed,
    fastForwardState: FastForwardIncrease
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.align(Alignment.End)) {
            Text(
                text = playSpeedState.label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = "speed",
                fontSize = 16.sp
            )
        }
        Row(modifier = Modifier.align(Alignment.End)) {
            Text(
                text = fastForwardState.label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = "FF",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DisplayControls(
    modifier: Modifier = Modifier,
    onPreviousClick: () -> Unit = { },
    onNextClick: () -> Unit = { },
    onFastRewindClick: () -> Unit = { },
    onFastForwardClick: () -> Unit = { },
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
                modifier = Modifier.fillMaxHeight().padding(end = 4.dp),
                colors = buttonColor,
                onClick = { onFastRewindClick() }) {
                Icon(
                    painterResource(id = R.drawable.ic_fast_rewind),
                    contentDescription = "rewind"
                )
            }
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
            Button(
                modifier = Modifier.fillMaxHeight().padding(start = 4.dp),
                colors = buttonColor,
                onClick = { onFastForwardClick() }
            ) {
                Icon(
                    painterResource(id = R.drawable.exo_icon_fastforward),
                    contentDescription = "fast_forward"
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
