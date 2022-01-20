package com.darekbx.workouts.data.di

import android.content.Context
import androidx.room.Room
import com.darekbx.workouts.data.WorkoutsDao
import com.darekbx.workouts.data.WorkoutsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class WorkoutsModule {

    @Provides
    fun provideWorkoutsDao(workoutsDatabase: WorkoutsDatabase): WorkoutsDao {
        return workoutsDatabase.workoutsDao()
    }

    @Provides
    fun provideWorkoutsDatabase(@ApplicationContext appContext: Context): WorkoutsDatabase {
        return Room.databaseBuilder(
            appContext,
            WorkoutsDatabase::class.java,
            WorkoutsDatabase.DB_NAME
        ).build()
    }
}
