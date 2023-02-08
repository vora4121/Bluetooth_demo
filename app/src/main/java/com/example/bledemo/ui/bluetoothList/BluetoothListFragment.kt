package com.example.bledemo.ui.bluetoothList

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bledemo.R
import com.example.bledemo.data.entities.BluetoothListModel
import com.example.bledemo.databinding.FragmentBluetoothListBinding
import com.example.bledemo.utils.Alert
import java.lang.reflect.Method
import java.util.*

class BluetoothListFragment : Fragment() {

    private val binding: FragmentBluetoothListBinding by lazy {
        FragmentBluetoothListBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var blAdapter: BluetoothListAdapter

    companion object {
        const val REQUEST_ENABLE_BT = 42
    }

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var arrBLList: ArrayList<String> = ArrayList()
    private var arrDevices: ArrayList<BluetoothDevice> = ArrayList()
    private var mGatt: BluetoothGatt? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBLSupport()
    }

    private fun validatePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                642
            )
        } else {
            scanBL()
        }
    }

    private fun scanBL() {
        setupRecycelerView()

        // TODO HERE WE ARE GETTING CONNECTED DEVICES
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            arrDevices.add(device)
            blAdapter.setItems(
                BluetoothListModel(
                    device.name, device.address, "", "", isConnected(device), ""
                )
            )
        }

        // TODO IF WE WANT TO DISCOVER DEVICES THEN WE NEED TO EXECUTE THIS METHODE
        /*
         if (bluetoothAdapter?.isDiscovering == true) {
             bluetoothAdapter?.cancelDiscovery()
         }

         var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
         filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
         filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
         requireActivity().registerReceiver(receiver, filter)
         bluetoothAdapter?.startDiscovery()*/
    }

    private fun setupRecycelerView() {
        binding.rvBlList.layoutManager = LinearLayoutManager(requireContext())
        blAdapter = BluetoothListAdapter {
            Alert("Device Connecting...")
            pairDevice(arrDevices[it])
        }
        binding.rvBlList.adapter = blAdapter
    }

    private fun checkBLSupport() {
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Alert("Device doesn't support Bluetooth")
        } else if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else if (bluetoothAdapter?.isEnabled == true) {
            // TODO HERE WE ARE GETTING CONNECTED DEVICES
            scanBL()
            // TODO IF WE WANT TO DISCOVER DEVICES THEN WE NEED TO EXECUTE THIS METHODE
            // validatePermission()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    var msg = ""
                    msg = if (deviceName.isNullOrBlank()) {
                        ""
                    } else {
                        "$deviceName"
                    }

                    if (!msg.isNullOrBlank() && !arrBLList.contains(msg)) {
                        arrBLList.add(msg)
                        blAdapter.setItems(
                            BluetoothListModel(
                                msg, deviceHardwareAddress, "", "", false, ""
                            )
                        )
                        binding.rvBlList.scrollToPosition(arrBLList.size - 1)
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    binding.progressBar.visibility = View.GONE
                    if (arrBLList.isEmpty()) {
                        Alert("Not found any Bluetooth devices")
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val m: Method = device.javaClass.getMethod("isConnected")
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                scanBL()
            } else {
                Alert("Please turn On bluetooth and restart app")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun pairDevice(device: BluetoothDevice) {

        mGatt = device.connectGatt(requireContext(), false, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    when(newState){

                        BluetoothProfile.STATE_CONNECTED -> {
                            Alert("Divice Connected")
                        }

                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Alert("Divice Disconnected")
                        }
                    }

                }else{
                    binding.progressBar.visibility = View.GONE
                    Alert("Device not available")
                }

            }

        })
    }

    private fun disconnect(device: BluetoothDevice) {
        val serviceListener: BluetoothProfile.ServiceListener = object :
            BluetoothProfile.ServiceListener {
            override fun onServiceDisconnected(profile: Int) {}

            @SuppressLint("DiscouragedPrivateApi")
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                val disconnect = BluetoothA2dp::class.java.getDeclaredMethod(
                    "disconnect",
                    BluetoothDevice::class.java
                )
                disconnect.isAccessible = true
                disconnect.invoke(proxy, device)
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
            }
        }
        BluetoothAdapter.getDefaultAdapter()
            .getProfileProxy(requireContext(), serviceListener, BluetoothProfile.A2DP)
    }
}