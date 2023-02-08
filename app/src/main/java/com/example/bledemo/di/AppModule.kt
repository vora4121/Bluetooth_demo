package com.example.bledemo.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.example.bledemo.data.local.AppDatabase
import com.example.bledemo.data.local.HistoryDao
import com.example.bledemo.data.repository.RoomRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun getAppDatabase(context: Application): AppDatabase {
        return AppDatabase.getAppDatabase(context)
    }

    @Provides
    @Singleton
    fun getAppDao(roomDatabase: AppDatabase): HistoryDao {
        return roomDatabase.historyDao()
    }

    @Singleton
    @Provides
    fun provideRepository(historyDao: HistoryDao) =
        RoomRepository(historyDao)



}