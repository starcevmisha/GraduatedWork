package com.example.bluetoothpasswordmanager

import android.content.Context
import org.json.JSONException
import org.json.JSONObject


class Password(
    val host: String,
    val url: String,
    val username: String,
    val password: String
) {

    companion object {

        fun getFromFile(filename: String, context: Context): ArrayList<Password> {
            val passwordsList = ArrayList<Password>()

            try {
                // Load data
                val jsonString = loadJsonFromAsset(filename, context)
                val json = JSONObject(jsonString)
                val passwords = json.getJSONArray("passwords")

                // Get Password objects from data
                (0 until passwords.length()).mapTo(passwordsList) {
                    val jsonObject = passwords.getJSONObject(it)
                    Password(
                        jsonObject.getString("host"),
                        jsonObject.getString("url"),
                        jsonObject.getString("username"),
                        jsonObject.getString("password")
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return passwordsList
        }

        private fun loadJsonFromAsset(filename: String, context: Context): String? {
            var json: String? = null

            try {
                val inputStream = context.assets.open(filename)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, Charsets.UTF_8)
            } catch (ex: java.io.IOException) {
                ex.printStackTrace()
                return null
            }

            return json
        }
    }
}