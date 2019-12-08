package com.example.bluetoothpasswordmanager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException

import java.util.*


private const val TAG = "BluetoothService"
private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


class BluetoothService {
    companion object {
        private var bTSocket: BluetoothSocket? = null
    }

    fun IsConnectedToDevice(): Boolean {
        return bTSocket != null
    }

    fun TryConnectToDevice(bTDevice: BluetoothDevice): Boolean {
        return ConnectThread().connect(bTDevice)
    }

    private inner class ConnectThread : Thread() {
        fun connect(bTDevice: BluetoothDevice): Boolean {
            bTSocket?.close()
            var temp: BluetoothSocket? = null
            try {
                temp = bTDevice.createRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                Log.d(TAG, "Could not create RFCOMM socket:$e")
                return false
            }
            bTSocket = temp
            try {
                bTSocket!!.connect()
            } catch (e: IOException) {
                Log.d(TAG, "Could not connect: $e")
                try {
                    bTSocket!!.close()
                } catch (close: IOException) {
                    Log.d(TAG, "Could not close connection:$e")
                }
                return false
            }

            return true
        }
    }

    fun cansel() {
        bTSocket?.close()
    }

    fun write(bytes: ByteArray) {
        try {
            bTSocket?.outputStream?.write(bytes)
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when sending data", e)
        }
    }
}
