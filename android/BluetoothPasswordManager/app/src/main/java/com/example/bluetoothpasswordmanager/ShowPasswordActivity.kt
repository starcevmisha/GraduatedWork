package com.example.bluetoothpasswordmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class ShowPasswordActivity : AppCompatActivity() {

    private lateinit var hostTextView: TextView
    private lateinit var urlTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var passwordTextView: TextView
    private var btService = BluetoothService()

    companion object {
        const val EXTRA_HOST = "title"
        const val EXTRA_URL = "url"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"

        fun newIntent(context: Context, password: Password): Intent {
            val detailIntent = Intent(context, ShowPasswordActivity::class.java)

            detailIntent.putExtra(EXTRA_HOST, password.host)
            detailIntent.putExtra(EXTRA_URL, password.url)
            detailIntent.putExtra(EXTRA_USERNAME, password.username)
            detailIntent.putExtra(EXTRA_PASSWORD, password.password)

            return detailIntent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val extras = intent.extras

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_detail)

        hostTextView = findViewById(R.id.password_details_host)
        hostTextView.text = extras?.getString(EXTRA_HOST)

        urlTextView = findViewById(R.id.password_details_url)
        urlTextView.text = extras?.getString(EXTRA_URL)

        usernameTextView = findViewById(R.id.password_details_username)
        usernameTextView.text = extras?.getString(EXTRA_USERNAME)

        passwordTextView = findViewById(R.id.password_details_password)

        passwordTextView.text = extras?.getString(EXTRA_PASSWORD)


        btService.write(extras?.getString(EXTRA_USERNAME)!!.toByteArray())
    }
}
