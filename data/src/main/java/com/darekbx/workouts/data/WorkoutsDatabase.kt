package com.darekbx.workouts.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.workouts.data.dto.Marker
import com.darekbx.workouts.data.dto.Workout

@Database(entities = [Workout::class, Marker::class], version = 1)
abstract class WorkoutsDatabase: RoomDatabase() {

    abstract fun workoutsDao(): WorkoutsDao

    companion object {
        val DB_NAME = "workouts_db"
    }
}
