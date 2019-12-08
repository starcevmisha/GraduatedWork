package com.example.bluetoothpasswordmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView


import android.net.Uri
import android.os.Parcelable
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.content.ContextCompat.startActivity
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var btService: BluetoothService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btService = BluetoothService()

        if (!btService.IsConnectedToDevice()) {
            val myIntent = Intent(this, ChooseBluetoothDeviceActivity::class.java)
            this.startActivity(myIntent)
        }

        //Get passwords list from Chrome
        if (intent?.action == Intent.ACTION_SEND) {
            val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
            Password.savePasswords(uri, this)
            intent?.setAction(null)
        }

        val passwordsList = Password.loadPasswords(this)
        if (passwordsList.size == 0) {
            val textView: TextView = findViewById(R.id.txt)
            textView.setText("List is Empty")
        }

        listView = findViewById(R.id.passwords_list_view)
        val adapter = PasswordAdapter(this, passwordsList)
        listView.adapter = adapter

        val context = this
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedPassword = passwordsList[position]
            val detailIntent = ShowPasswordActivity.newIntent(context, selectedPassword)
            startActivity(detailIntent)
        }

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete_passwords) {
            Password.deletePasswords(this)

            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);

            return true
        }
        if (item.itemId == R.id.action_connect_to_blueetoth_device) {
            val myIntent = Intent(this, ChooseBluetoothDeviceActivity::class.java)
            this.startActivity(myIntent)
            return true
        }

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        btService.cansel()
        super.onDestroy()
    }

}
