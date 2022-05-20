package com.darekbx.workouts.viewmodels

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.darekbx.workouts.data.WorkoutsDao
import com.darekbx.workouts.data.dto.MarkerDto
import com.darekbx.workouts.data.dto.WorkoutDto
import com.darekbx.workouts.model.Marker
import com.darekbx.workouts.model.Marker.Companion.toDomain
import com.darekbx.workouts.model.Workout
import com.darekbx.workouts.model.Workout.Companion.toDomain
import com.darekbx.workouts.utils.FastForwardIncrease
import com.darekbx.workouts.utils.PlaybackSpeed
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutsDao: WorkoutsDao,
    @ApplicationContext private val context: Context,
): ViewModel() {

    var movieFile = mutableStateOf<String?>(null)

    fun copyFile(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fileName = computeMd5(uri.toString())
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    context.openFileOutput(fileName, MODE_PRIVATE)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                        movieFile.value = fileName
                    }
                }
            }
        }
    }

    fun persistPlaybackSettings(
        playbackSpeed: PlaybackSpeed,
        fastForwardIncrease: FastForwardIncrease
    ) {
        playbackSpeedState = playbackSpeed
        fastForwardIncreaseState = fastForwardIncrease
    }

    fun workout(uid: String) =
        Transformations.map(workoutsDao.workout(uid)) { it.toDomain() }

    fun markAsPlayed(uid: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                workoutsDao.markWorkoutAsPlayed(uid)
            }
        }
    }

    fun loadPlaybackSettings(): Pair<PlaybackSpeed, FastForwardIncrease> {
        return Pair(playbackSpeedState, fastForwardIncreaseState)
    }

    fun workouts(): LiveData<List<Workout>> = Transformations.map(workoutsDao.workouts()) { dtos ->
        dtos.map { it.toDomain() }
    }

    fun workoutMarkers(uid: String): LiveData<List<Marker>> =
        Transformations.map(workoutsDao.workoutMarkers(uid)) { dtos ->
            dtos.map { it.toDomain() }
        }

    fun add(
        name: String,
        uri: String,
        length: Long,
        previewFrame: Bitmap,
        markers: List<Long>,
        onCompleted: () -> Unit = { }
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val workoutUid = UUID.randomUUID().toString()
                val workout = WorkoutDto(
                    workoutUid,
                    name,
                    uri,
                    lastPlayed = 0,
                    timesPlayed = 0,
                    length,
                    previewFrame.toByteArray()
                )
                workoutsDao.addWorkout(workout)
                markers.forEach { time ->
                    workoutsDao.addMarker(
                        MarkerDto(
                            UUID.randomUUID().toString(),
                            workoutUid,
                            time,
                            0
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    onCompleted()
                }
            }
        }
    }

    fun delete(workout: Workout) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                workoutsDao.deleteWorkoutMarkers(workout.uid)
                workoutsDao.deleteWorkout(workout.uid)
                File(context.filesDir, workout.moviePath).delete()
            }
        }
    }

    private fun Bitmap.toByteArray(): ByteArray {
        ByteArrayOutputStream().use { stream ->
            compress(Bitmap.CompressFormat.PNG, 90, stream)
            return stream.toByteArray()
        }
    }

    private fun computeMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    companion object {
        private var playbackSpeedState: PlaybackSpeed = PlaybackSpeed.SPEED_1_0
        private var fastForwardIncreaseState: FastForwardIncrease = FastForwardIncrease.FF_10
    }
}
