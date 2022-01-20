package com.darekbx.workouts.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout")
data class Workout(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "movie_path") val moviePath: String,
    /**
     * When last played
     */
    @ColumnInfo(name = "last_played") val lastPlayed: Long,
    /**
     * How many times played
     */
    @ColumnInfo(name = "times_played") val timesPlayed: Int,
    /**
     * Workout length in seconds
     */
    @ColumnInfo(name = "length") val length: Long,
    @ColumnInfo(name = "thumbnail", typeAffinity = ColumnInfo.BLOB) val thumbnail: ByteArray
)
