package com.darekbx.workouts.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.workouts.data.WorkoutsDao
import com.darekbx.workouts.data.dto.MarkerDto
import com.darekbx.workouts.data.dto.WorkoutDto
import com.darekbx.workouts.model.Workout
import com.darekbx.workouts.model.Workout.Companion.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutsDao: WorkoutsDao
): ViewModel() {

    fun workouts(): LiveData<List<Workout>> = Transformations.map(workoutsDao.workouts()) { dtos ->
        dtos.map { it.toDomain() }
    }

    fun add(workoutDto: WorkoutDto) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                workoutsDao.addWorkout(workoutDto)
            }
        }
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
                    name!!,
                    uri,
                    lastPlayed = 0,
                    timesPlayed = 0,
                    length,
                    previewFrame.toByteArray()
                )
                workoutsDao.addWorkout(workout)
                markers.forEach { time ->
                    workoutsDao.addMarker(MarkerDto(
                        UUID.randomUUID().toString(),
                        workoutUid,
                        time,
                        0
                    ))
                }
                withContext(Dispatchers.Main) {
                    onCompleted()
                }
            }
        }
    }

    private fun Bitmap.toByteArray(): ByteArray {
        ByteArrayOutputStream().use { stream ->
            compress(Bitmap.CompressFormat.JPEG, 90, stream)
            val byteArray: ByteArray = stream.toByteArray()
            //recycle()
            return byteArray
        }
    }
}
