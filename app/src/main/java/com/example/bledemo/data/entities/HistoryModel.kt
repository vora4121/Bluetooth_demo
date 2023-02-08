package com.example.bledemo.data.entities

import androidx.room.PrimaryKey

@androidx.room.Entity(tableName = "connected_device_history")
data class HistoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val deviceName: String = "",
    val deviceAddress: String = "",
    val deviceConnectedTime: String = "",
    val deviceDisConnectTime: String = "",
    val totalConnectedDuration: String = ""
)