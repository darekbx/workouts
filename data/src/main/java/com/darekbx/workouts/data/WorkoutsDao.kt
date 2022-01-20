package com.darekbx.workouts.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.workouts.data.dto.Marker
import com.darekbx.workouts.data.dto.Workout

@Dao
interface WorkoutsDao {

    @Query("SELECT * FROM workout")
    fun workouts(): List<Workout>

    @Query("SELECT * FROM marker")
    fun markers(): List<Marker>

    @Insert
    fun addMarker(marker: Marker): Long
}
