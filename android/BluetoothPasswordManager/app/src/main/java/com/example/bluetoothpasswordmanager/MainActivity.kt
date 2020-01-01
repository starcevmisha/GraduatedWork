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
import android.widget.SearchView


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var btService: BluetoothService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btService = BluetoothService()

//        if (!btService.IsConnectedToDevice()) {
//            val chooseBtDeviceActivity = Intent(this, ChooseBluetoothDeviceActivity::class.java)
//            this.startActivity(chooseBtDeviceActivity)
//        }

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

        val context = this

        listView = findViewById(R.id.passwords_list_view)
        val adapter = PasswordAdapter(this, passwordsList)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedPassword = adapter.getItem(position) as Password
            val detailIntent = ShowPasswordActivity.newIntent(context, selectedPassword)
            startActivity(detailIntent)
        }


        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(context)

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


    override fun onQueryTextSubmit(s: String): Boolean {
        (listView.adapter as PasswordAdapter).filter.filter(s)
        return false
    }

    override fun onQueryTextChange(s: String): Boolean {
        (listView.adapter as PasswordAdapter).filter.filter(s)
        return false
    }
}
