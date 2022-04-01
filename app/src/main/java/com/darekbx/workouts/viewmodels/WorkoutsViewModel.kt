package com.darekbx.workouts.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.workouts.data.WorkoutsDao
import com.darekbx.workouts.data.dto.Marker
import com.darekbx.workouts.data.dto.Workout
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

    fun workouts(): LiveData<List<Workout>> = workoutsDao.workouts()

    fun add(workout: Workout) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                workoutsDao.addWorkout(workout)
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
                val workout = Workout(
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
                    workoutsDao.addMarker(Marker(
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
