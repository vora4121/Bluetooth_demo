package com.example.bledemo.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bledemo.data.entities.HistoryModel

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryData(historyModel: HistoryModel)

    @Query("SELECT * FROM connected_device_history")
    fun getHistoryData(): LiveData<List<HistoryModel>>

}