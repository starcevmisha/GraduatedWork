package com.example.bluetoothpasswordmanager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.math.BigInteger


import java.util.*


private const val TAG = "BluetoothService"
private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

private val p : Long = 2147483647
private val g : Long = 16807



class BluetoothService {
    private var secretKey : Long = 0
    companion object {
        private var bTSocket: BluetoothSocket? = null
    }

    fun IsConnectedToDevice(): Boolean {
        return bTSocket != null
    }

    fun TryConnectToDevice(bTDevice: BluetoothDevice, isSecure:Boolean): Boolean {
        return ConnectThread().connect(bTDevice, isSecure)
    }

    private inner class ConnectThread : Thread() {
        fun connect(bTDevice: BluetoothDevice, isSecure: Boolean): Boolean {
            secretKey = 0
            bTSocket?.close()
            val temp: BluetoothSocket?
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

            if (isSecure) {
                val a = (1..p).random() // Diffie-Hellman privateKey
                val A = BigInteger.valueOf(g).modPow(BigInteger.valueOf(a), BigInteger.valueOf(p)).toLong()
                val message = "dhs p=$p g=$g A=$A\n"
                bTSocket!!.outputStream!!.write(message.toByteArray())
                sleep(100)
                if (bTSocket!!.inputStream!!.available() > 0){
                    val available = bTSocket!!.inputStream!!.available()
                    val bytes = ByteArray(available)
                    Log.i("server", "Reading")
                    bTSocket!!.inputStream!!.read(bytes, 0, available)
                    val text = String(bytes)
                    Log.i("server", "Message received")
                    Log.i("server", "B= $text")

                    val B = text.toLongOrNull()
                    if (B != null) {
                        secretKey = BigInteger.valueOf(B).modPow(BigInteger.valueOf(a), BigInteger.valueOf(p)).toLong()
                        Log.i("server", "secretKey= $secretKey")
                    }
                }


            }

            return true
        }
    }

    fun cansel() {
        bTSocket?.close()
    }

    fun write(bytes: ByteArray) {
        try {
            if (secretKey > 0) {
                //code bytes
            }
            bTSocket?.outputStream?.write(bytes)
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when sending data", e)
        }
    }
}
