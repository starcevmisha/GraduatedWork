package com.example.bluetoothpasswordmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothAdapter
import android.widget.ListView
import android.content.Intent
import androidx.appcompat.widget.Toolbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import kotlin.collections.ArrayList
import com.google.android.material.snackbar.Snackbar

class ChooseBluetoothDeviceActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var toolbar: Toolbar
    private lateinit var btService :BluetoothService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_bluetooth_device)

        listView = findViewById(R.id.bluetooth_list_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.title = "Choose device"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }


        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!bluetoothAdapter.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 12)
        }

        val pairedDevices = bluetoothAdapter.bondedDevices
        val deviceList = ArrayList<BtDevice>()
        for (bt in pairedDevices)
            deviceList.add(BtDevice(bt.name, bt.address))

        val adapter = BtDeviceAdapter(
            this,
            deviceList
        )

        listView.adapter = adapter

        btService = BluetoothService()

        listView.setOnItemClickListener { _, _, position, _ ->
            toast("Try to connect to " + deviceList[position])
            doAsync {
                val result = btService.TryConnectToDevice(bluetoothAdapter.getRemoteDevice(deviceList[position].address), true)
                uiThread {
                    if (result) {
                        finish()
                    } else {
                        val snackbar = Snackbar
                            .make(listView, "Can't connect to this device", Snackbar.LENGTH_LONG)
                        snackbar.show()

                    }
                }
            }
        }
    }
}

