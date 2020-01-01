package com.example.bluetoothpasswordmanager

import android.app.SearchManager
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import androidx.core.view.MenuItemCompat.setOnActionExpandListener
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var btService: BluetoothService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btService = BluetoothService()

        if (!btService.IsConnectedToDevice()) {
            val chooseBtDeviceActivity = Intent(this, ChooseBluetoothDeviceActivity::class.java)
            this.startActivity(chooseBtDeviceActivity)
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

        val context = this

        listView = findViewById(R.id.passwords_list_view)
        val adapter = PasswordAdapter(this, passwordsList)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedPassword = adapter.getItem(position) as Password
            val detailIntent = ShowPasswordActivity.newIntent(context, selectedPassword)
            startActivity(detailIntent)
        }

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        searchView = searchMenuItem.getActionView() as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(this)
        searchView.isIconifiedByDefault = false
        searchView.isFocusable = true
        searchView.isIconified = false

        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                showKeyboard()
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                hideKeyboard()
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete_passwords) {
            Password.deletePasswords(this)

            finish()
            overridePendingTransition(0, 0)
            startActivity(getIntent())
            overridePendingTransition(0, 0)

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

    private fun showKeyboard(){
        searchView.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm!!.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }
}
