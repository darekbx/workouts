package com.darekbx.workouts.model

import com.darekbx.workouts.data.dto.MarkerDto

data class Marker(
    val uid: String,
    val workoutUid: String,
    val time: Long,
    val timesPlayed: Int
) {
    companion object {
        fun MarkerDto.toDomain(): Marker {
            return Marker(uid, workoutUid, time, timesPlayed)
        }
    }
}
