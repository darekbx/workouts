package com.darekbx.workouts.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.workouts.data.dto.Marker
import com.darekbx.workouts.data.dto.Workout

@Dao
interface WorkoutsDao {

    @Query("SELECT * FROM workout")
    fun workouts(): LiveData<List<Workout>>

    @Insert
    fun addWorkout(workout: Workout): Long

    @Query("SELECT * FROM marker")
    fun markers(): LiveData<List<Marker>>

    @Insert
    fun addMarker(marker: Marker): Long
}
