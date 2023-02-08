package com.example.bledemo.utils

import android.app.Activity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment


fun Activity.Alert(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.Alert(message: String){
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}