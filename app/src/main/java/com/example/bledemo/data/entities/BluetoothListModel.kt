package com.example.bledemo.data.entities

data class BluetoothListModel(
    val deviceName: String = "",
    val deviceAddress: String = "",
    val deviceConnectedTime: String = "",
    val deviceDisConnectTime: String = "",
    val isDeviceConnected: Boolean = false,
    val totalConnectedDuration: String = ""
)
