package com.darekbx.workouts.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.workouts.data.dto.MarkerDto
import com.darekbx.workouts.data.dto.WorkoutDto

@Database(entities = [WorkoutDto::class, MarkerDto::class], version = 1)
abstract class WorkoutsDatabase: RoomDatabase() {

    abstract fun workoutsDao(): WorkoutsDao

    companion object {
        val DB_NAME = "workouts_db"
    }
}
