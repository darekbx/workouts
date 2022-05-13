package com.darekbx.workouts.ui.settings

import android.content.Context
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
import android.media.MediaMetadataRetriever

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.darekbx.workouts.utils.toFormattedTime
import com.darekbx.workouts.utils.toSeconds
import kotlinx.coroutines.launch
import java.io.File
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

@Preview
@Composable
fun EditWorkout(
    workoutsViewModel: WorkoutsViewModel = hiltViewModel(),
    workoutUid: String? = null,
    onCompleted: () -> Unit = { }
) {
    val markers = remember { mutableStateListOf<Long>() }
    val name = remember { mutableStateOf("") }
    var previewFrame by remember { mutableStateOf(null as Bitmap?) }
    val movieLength = remember { mutableStateOf(0L) }

    val selectFrameDialogState = remember { mutableStateOf(false) }
    val setMarkersDialogState = remember { mutableStateOf(false) }
    var progressVisible by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { workoutsViewModel.copyFile(uri) } }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.padding(all = 8.dp)) {
            WorkoutName(Modifier.fillMaxWidth(), name)
            MovieSelectButton(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                workoutsViewModel.movieFile.value
            ) { launcher.launch("*/*") }

            Spacer(modifier = Modifier.height(8.dp))

            workoutsViewModel.movieFile.value?.let {
                SelectPreviewFrameButton(
                    Modifier.fillMaxWidth(),
                    selectFrameDialogState,
                    previewFrame
                )
                SetMarkersButton(
                    Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    setMarkersDialogState,
                    markers
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = markers.size > 0 && previewFrame != null && name.value.isNotEmpty(),
                onClick = {
                    progressVisible = false
                    workoutsViewModel.add(
                        name.value,
                        workoutsViewModel.movieFile.value!!,
                        movieLength.value,
                        previewFrame!!,
                        markers
                    ) {
                        progressVisible = false
                        onCompleted()
                    }
                }
            ) { Text(text = "Save") }
        }

        if (progressVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.33F)),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
    }

    if (selectFrameDialogState.value) {
        workoutsViewModel.movieFile.value?.let {
            SelectPreviewFrame(
                movieFile = it,
                frameCallback = { frame ->
                    previewFrame = frame
                    selectFrameDialogState.value = false
                },
                movieLength = { length -> movieLength.value = length }
            )
        }
    }

    if (setMarkersDialogState.value) {
        workoutsViewModel.movieFile.value?.let {
            SetMarkers(
                movieFile = it,
                markersCallback = { markersList ->
                    markers.clear()
                    markers.addAll(markersList)
                    setMarkersDialogState.value = false
                }
            )
        }
    }
}

@Composable
private fun SelectPreviewFrameButton(
    modifier: Modifier = Modifier,
    selectFrameDialogState: MutableState<Boolean>,
    previewFrame: Bitmap?
) {
    Button(
        modifier = modifier,
        onClick = { selectFrameDialogState.value = true }
    ) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = "Select preview frame")
            if (previewFrame != null) {
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }
    }
}

@Composable
private fun SetMarkersButton(
    modifier: Modifier = Modifier,
    setMarkersDialogState: MutableState<Boolean>,
    markers: List<Long>
) {
    Button(
        modifier = modifier,
        onClick = { setMarkersDialogState.value = true }
    ) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = "Set markers")
            if (markers.isNotEmpty()) {
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "(${markers.size})",
                    style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                )
            }
        }
    }
}

@Composable
private fun WorkoutName(modifier: Modifier = Modifier, name: MutableState<String>) {
    TextField(
        modifier = modifier,
        value = name.value,
        onValueChange = { name.value = it },
        label = { Text(stringResource(id = R.string.movie_name_hint)) }
    )
}

@Composable
private fun SelectPreviewFrame(
    movieFile: String,
    frameCallback: (Bitmap) -> Unit,
    movieLength: (Long) -> Unit
) {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(computeMovieUrl(context, movieFile))
    val playWhenReady by rememberSaveable { mutableStateOf(true) }
    player.setMediaItem(mediaItem)
    playerView.player = player

    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady
    }

    var videoFrame by remember { mutableStateOf(null as Bitmap?) }

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(8.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .weight(1F)
                .padding(16.dp),
            factory = {
                playerView
            })

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                onClick = {
                    playerView.player?.pause()
                    playerView.hideController()

                    getVideoFrame(context, movieFile, player.currentPosition)?.let {
                        videoFrame = it
                    }
                }
            ) { Text("Select frame") }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                onClick = {
                    movieLength(playerView.player?.duration ?: 0L)
                    frameCallback(videoFrame!!)
                },
                enabled = videoFrame != null
            ) { Text("Confirm") }
        }
    }
}

@Composable
private fun SetMarkers(
    movieFile: String,
    markersCallback: (List<Long>) -> Unit
) {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(computeMovieUrl(context, movieFile))
    val playWhenReady by rememberSaveable { mutableStateOf(true) }
    player.setMediaItem(mediaItem)
    playerView.player = player

    playerView.controllerShowTimeoutMs = 0
    playerView.controllerHideOnTouch = false

    val markers = remember { mutableStateListOf<Long>() }

    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(8.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .weight(1F)
                .padding(16.dp),
            factory = { playerView }
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = "Markers"
        )
        Markers(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(top = 8.dp),
            markers
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            AddMarkerButton(
                Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                onAddMarker = { markers.add(it) },
                playerView
            )
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                onClick = { markersCallback(markers) },
                enabled = markers.isNotEmpty()
            ) { Text("Confirm") }
        }
    }
}

@Composable
private fun computeMovieUrl(
    context: Context,
    movieFile: String
) = File(context.filesDir, movieFile).toUri()

@Composable
private fun Markers(
    modifier: Modifier = Modifier,
    markers: SnapshotStateList<Long>
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(markers.size) {
        coroutineScope.launch {
            listState.animateScrollToItem(markers.size)
        }
    }
    LazyColumn(
        modifier,
        state = listState
    ) {
        itemsIndexed(markers) { index, time ->
            val color = if (index % 2 == 0) Color.White.copy(alpha = 0.075f) else Color.Transparent
            val timeString = time.toFormattedTime()
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .padding(4.dp),
                text = "#${index + 1}: $timeString"
            )
        }
    }
}

@Composable
private fun MovieSelectButton(
    modifier: Modifier = Modifier,
    movieFile: String?,
    onClick: () -> Unit = { }
) {
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
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_movie),
                    contentDescription = "Favorite",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(id = R.string.select_movie))
            }
            if (movieFile != null) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = movieFile.toString(),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AddMarkerButton(
    modifier: Modifier = Modifier,
    onAddMarker: (Long) -> Unit,
    playerView: PlayerView
) {
    val positionView = remember {playerView.findViewById<TextView>(R.id.exo_position)}
    Button(
        modifier = modifier,
        onClick = { onAddMarker(positionView.text.toString().toSeconds() * 1000L) }
    ) { Text(text = "Add marker") }
}

private fun getVideoFrame(context: Context, movieFile: String, time: Long): Bitmap? {
    var bitmap: Bitmap? = null
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(File(context.filesDir, movieFile).absolutePath)
        bitmap = retriever.getFrameAtTime(time * 1000) // In microseconds
    } catch (ex: RuntimeException) {
        ex.printStackTrace()
    } finally {
        try {
            retriever.release()
        } catch (ex: RuntimeException) { }
    }
    return bitmap
}
