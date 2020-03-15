package com.example.bluetoothpasswordmanager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException


import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private val AES_SECRET_KEY = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F)

private const val TAG = "BluetoothService"
private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")



private fun encrypt(raw: ByteArray, clear: ByteArray): ByteArray {
    val skeySpec = SecretKeySpec(raw, "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
    return cipher.doFinal(clear)
}

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

//            if (isSecure) {
////                val a = (1..p).random() // Diffie-Hellman privateKey
////                val A = BigInteger.valueOf(g).modPow(BigInteger.valueOf(a), BigInteger.valueOf(p)).toLong()
////                val message = "dhs p=$p g=$g A=$A\n"
////                bTSocket!!.outputStream!!.write(message.toByteArray())
////                sleep(100)
////                if (bTSocket!!.inputStream!!.available() > 0){
////                    val available = bTSocket!!.inputStream!!.available()
////                    val bytes = ByteArray(available)
////                    Log.i("server", "Reading")
////                    bTSocket!!.inputStream!!.read(bytes, 0, available)
////                    val text = String(bytes)
////                    Log.i("server", "Message received")
////                    Log.i("server", "B= $text")
////
////                    val B = text.toLongOrNull()
////                    if (B != null) {
////                        secretKey = BigInteger.valueOf(B).modPow(BigInteger.valueOf(a), BigInteger.valueOf(p)).toLong()
////                        Log.i("server", "secretKey= $secretKey")
////                    }
////                }
////
////
////            }


            return true
        }
    }

    fun cansel() {
        bTSocket?.close()
    }

    fun write(bytes: ByteArray) {
        try {
            val encrypted = encrypt(AES_SECRET_KEY, bytes)
            bTSocket?.outputStream?.write(encrypted)
//            bTSocket?.outputStream?.write(bytes)
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when sending data", e)
        }
    }
}
