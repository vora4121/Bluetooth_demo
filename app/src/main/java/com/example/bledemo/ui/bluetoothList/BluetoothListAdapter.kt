package com.example.bledemo.ui.bluetoothList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bledemo.R
import com.example.bledemo.data.entities.BluetoothListModel
import com.example.bledemo.data.entities.HistoryModel
import com.example.bledemo.databinding.ItemBlBinding

class BluetoothListAdapter(val listner: (position: Int)-> Unit) : RecyclerView.Adapter<BluetoothListAdapter.ViewHolder>() {

    private var arrBLList: ArrayList<BluetoothListModel> = ArrayList()

    fun setItems(historyModel: BluetoothListModel) {
        arrBLList.add(historyModel)
        notifyItemInserted(arrBLList.size - 1)
    }

    class ViewHolder(val itemBinding: ItemBlBinding, val listner: (position: Int)-> Unit) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(blListModel: BluetoothListModel, position: Int) {
            itemBinding.tvName.text = blListModel.deviceName
            itemBinding.tvDescription.text = blListModel.deviceAddress
            if (blListModel.isDeviceConnected) {
                itemBinding.tvConnect.setBackgroundResource(R.drawable.round_connected_bg)
            }

            itemBinding.tvConnect.setOnClickListener {
                listner.invoke(position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemBlBinding =
            ItemBlBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listner)
    }

    override fun getItemCount(): Int = arrBLList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrBLList[position], position)
    }

}