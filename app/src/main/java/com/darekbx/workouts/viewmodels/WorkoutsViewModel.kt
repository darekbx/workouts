package com.darekbx.workouts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.workouts.data.WorkoutsDao
import com.darekbx.workouts.data.dto.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
}
