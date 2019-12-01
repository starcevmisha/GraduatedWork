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


class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent?.action == Intent.ACTION_SEND) {
            val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
            Password.savePasswords(uri, this)
            intent?.setAction(null)
        }

        val passwordsList = Password.loadPasswords(this)
        if (passwordsList.size == 0){
            val textView:TextView = findViewById(R.id.txt)
            textView.setText("List is Empty")
        }

        listView = findViewById(R.id.recipe_list_view)
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId ==  R.id.action_delete_passwords){
            Password.deletePasswords(this)

            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);

            return true
        }
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }

    }

}
