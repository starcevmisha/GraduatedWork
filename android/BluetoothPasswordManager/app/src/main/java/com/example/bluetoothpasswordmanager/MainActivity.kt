package com.example.bluetoothpasswordmanager

import android.app.Activity
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
import android.app.KeyguardManager







class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var btService: BluetoothService

    companion object {
        private var isAuth = false
        private var skipBtConnectedDeviseCheck = false;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isAuth) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val km = this.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val intent = km.createConfirmDeviceCredentialIntent(
                    resources.getString(R.string.app_name),
                    "Enter your password to enter this App."
                )

                // launch the intent
                startActivityForResult(intent, 1234)
                return
            }
        }

        btService = BluetoothService()
        if (!skipBtConnectedDeviseCheck && !btService.IsConnectedToDevice()) {
            skipBtConnectedDeviseCheck = true
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

    // call back when password is correct
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1234) {
            if (resultCode == Activity.RESULT_OK) {
                isAuth = true
                //restart activity
                finish()
                startActivity(intent)
            } else {
                finish()
            }


        }
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
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        if (::btService.isInitialized){
            btService.cansel()
        }
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
