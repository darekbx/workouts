package com.darekbx.workouts.ui.settings

import android.content.Context
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
import android.media.MediaMetadataRetriever

import android.graphics.Bitmap
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
import kotlinx.coroutines.launch
import java.lang.RuntimeException

@Preview
@Composable
fun EditWorkout(
    workoutsViewModel: WorkoutsViewModel = hiltViewModel(),
    workoutUid: String? = null,
    onCompleted: () -> Unit = { }
) {
    val markers = remember { mutableStateListOf<Long>() }
    var movieUri by remember { mutableStateOf(null as Uri?) }
    val name = remember { mutableStateOf("") }
    var previewFrame by remember { mutableStateOf(null as Bitmap?) }
    val movieLength = remember { mutableStateOf(0L) }

    val selectFrameDialogState = remember { mutableStateOf(false) }
    val setMarkersDialogState = remember { mutableStateOf(false) }
    var progressVisible by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { movieUri = it }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.padding(all = 8.dp)) {
            WorkoutName(Modifier.fillMaxWidth(), name)
            MovieSelectButton(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                movieUri
            ) { launcher.launch("*/*") }

            Spacer(modifier = Modifier.height(8.dp))

            movieUri?.let {
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
                        movieUri?.toString()!!,
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
        movieUri?.let {
            SelectPreviewFrame(
                movieUri = it,
                frameCallback = { frame ->
                    previewFrame = frame
                    selectFrameDialogState.value = false
                },
                movieLength = { length -> movieLength.value = length }
            )
        }
    }

    if (setMarkersDialogState.value) {
        movieUri?.let {
            SetMarkers(
                movieUri = it,
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
    movieUri: Uri,
    frameCallback: (Bitmap) -> Unit,
    movieLength: (Long) -> Unit
) {
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

                    getVideoFrame(context, movieUri, player.currentPosition)?.let {
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
    movieUri: Uri,
    markersCallback: (List<Long>) -> Unit
) {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(movieUri)
    val playWhenReady by rememberSaveable { mutableStateOf(true) }
    player.setMediaItem(mediaItem)
    playerView.player = player

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
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            AddMarkerButton(
                Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                onAddMarker = { markers.add(it) },
                player
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
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .padding(4.dp),
                text = "#${index + 1}: ${time / 1000}s"
            )
        }
    }
}

@Composable
private fun MovieSelectButton(
    modifier: Modifier = Modifier,
    movieUri: Uri?,
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
            if (movieUri != null) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = movieUri.toString(),
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
    player: SimpleExoPlayer
) {
    Button(
        modifier = modifier,
        onClick = { onAddMarker(player.currentPosition) }
    ) { Text(text = "Add marker") }
}

private fun getVideoFrame(context: Context, uri: Uri, time: Long): Bitmap? {
    var bitmap: Bitmap? = null
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, uri)
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
