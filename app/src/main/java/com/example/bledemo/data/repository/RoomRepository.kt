package com.example.bledemo.data.repository

import androidx.lifecycle.LiveData
import com.example.bledemo.data.entities.HistoryModel
import com.example.bledemo.data.local.HistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomRepository @Inject constructor(private val historyDao: HistoryDao) {

    suspend fun insertHistoryData(historyModel: HistoryModel) = withContext(Dispatchers.IO) {
        historyDao.insertHistoryData(historyModel)
    }

    fun getHistoryData(): LiveData<List<HistoryModel>> {
        return historyDao.getHistoryData()
    }

}