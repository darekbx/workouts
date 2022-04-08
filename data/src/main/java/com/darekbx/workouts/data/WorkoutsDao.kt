package com.darekbx.workouts.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.workouts.data.dto.MarkerDto
import com.darekbx.workouts.data.dto.WorkoutDto

@Dao
interface WorkoutsDao {

    @Query("SELECT * FROM workout")
    fun workouts(): LiveData<List<WorkoutDto>>

    @Insert
    fun addWorkout(workoutDto: WorkoutDto): Long

    @Query("DELETE FROM workout WHERE uid = :uid")
    fun deleteWorkout(uid: String)

    @Query("DELETE FROM marker WHERE workout_uid = :uid")
    fun deleteWorkoutMarkers(uid: String)

    @Query("SELECT * FROM marker")
    fun markers(): LiveData<List<MarkerDto>>

    @Insert
    fun addMarker(markerDto: MarkerDto): Long
}
