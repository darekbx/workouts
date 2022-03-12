package com.darekbx.workouts.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.R
import com.darekbx.workouts.viewmodels.WorkoutsViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

@Preview
@Composable
fun EditWorkout(
    workoutsViewModel: WorkoutsViewModel = hiltViewModel(),
    workoutUid: String? = null
) {
    var movieUri by remember { mutableStateOf(null as? Uri) }
    var markerTime by remember { mutableStateOf(0L) }

    var launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        movieUri = it
    }

    Column(Modifier.padding(all = 8.dp)) {
        WorkoutName(Modifier.fillMaxWidth())
        MovieSelectButton(
            Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            { launcher.launch("*/*") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        WorkoutPlayer(
            Modifier
                .fillMaxWidth()
                .height(300.dp), movieUri) { marker ->
            markerTime = marker
        }
        Text(text = "Time: ${markerTime / 1000}s")
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
        onClick = { onClick() },
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

@Composable
fun WorkoutPlayer(
    modifier: Modifier = Modifier,
    uri: Uri?,
    onAddMarker: (Long) -> Unit
) {
    if (uri == null) return

    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(uri)
    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }
    player.setMediaItem(mediaItem)
    playerView.player = player

    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady
    }

    Column(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize().weight(1F),
            factory = {
                playerView
            })
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            onAddMarker(player.currentPosition)
        }) {
            Text(text = "Add marker")
        }
    }
}
