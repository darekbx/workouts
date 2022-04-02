package com.darekbx.workouts.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.darekbx.workouts.data.dto.WorkoutDto

data class Workout(
    val uid: String,
    val name: String,
    val moviePath: String,
    val lastPlayed: Long,
    val timesPlayed: Int,
    val length: Long,
    val thumbnail: Bitmap
) {
    companion object {
        fun WorkoutDto.toDomain(): Workout {
            val thumbnailBitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.size)
            return Workout(uid, name, moviePath, lastPlayed, timesPlayed, length, thumbnailBitmap)
        }
    }
}
