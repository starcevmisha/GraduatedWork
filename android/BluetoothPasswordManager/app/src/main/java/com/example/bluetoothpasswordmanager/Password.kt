package com.example.bluetoothpasswordmanager
import com.opencsv.CSVReader
import android.content.Context
import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileNotFoundException
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream




data class Password(
    val host: String,
    val url: String,
    val username: String,
    val password: String
) {
    companion object {
        private const val filename: String = "passwords.json";

        fun savePasswords(uri: Uri, context: Context) {
            val inputPFD = try {
                context.contentResolver.openFileDescriptor(uri, "r")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Log.e("MainActivity", "File not found.")
                return
            }
            val fd = inputPFD?.fileDescriptor
            val inputStream = FileInputStream(fd)

            val csvReader = CSVReader(inputStream.bufferedReader())


            val jsonPasswordsArray = JSONArray()

            csvReader.readNext() // skip first header line
            var line: Array<String>? = csvReader.readNext()
            while (line != null) {

                val jsonPassword = JSONObject()
                jsonPassword.put("host", line[0])
                jsonPassword.put("url",line[1])
                jsonPassword.put("username", line[2])
                jsonPassword.put("password", "password")
                jsonPasswordsArray.put(jsonPassword)

                line = csvReader.readNext()
            }
            val jsonString = jsonPasswordsArray.toString()

            val path = context.filesDir
            val file = File(path, filename)
            val stream = FileOutputStream(file)
            stream.use { stream ->
                stream.write(jsonString.toByteArray())
            }
        }

        fun loadPasswords(context: Context): ArrayList<Password> {
            val path = context.filesDir
            val file = File(path, filename)
            val length = file.length().toInt()
            val bytes = ByteArray(length)
            val input = FileInputStream(file)
            try {
                input.read(bytes)
            } finally {
                input.close()
            }

            val jsonString = String(bytes)


            val passwordsList = ArrayList<Password>()

            try {
                val jsonArray = JSONArray(jsonString)

                // Get Password objects from data
                (0 until jsonArray.length()).mapTo(passwordsList) {
                    val jsonObject = jsonArray.getJSONObject(it)
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

            passwordsList.sortBy { it.host }
            return passwordsList
        }
    }
}