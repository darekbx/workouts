package com.darekbx.workouts.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marker")
class MarkerDto(
    @PrimaryKey
    val uid: String,
    @ColumnInfo(name = "workout_uid") val workoutUid: String,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "times_played") val timesPlayed: Int
)
