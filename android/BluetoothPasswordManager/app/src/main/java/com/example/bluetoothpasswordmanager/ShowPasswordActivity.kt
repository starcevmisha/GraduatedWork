package com.example.bluetoothpasswordmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_main.*


class ShowPasswordActivity : AppCompatActivity() {

    private lateinit var hostTextView: TextView
    private lateinit var urlTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var passwordTextView: TextView
    private var IsPasswordShowed: Boolean = false
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

        this.title = extras?.getString(EXTRA_HOST)

        usernameTextView = findViewById(R.id.password_details_username)
        usernameTextView.text = extras?.getString(EXTRA_USERNAME)

        passwordTextView = findViewById(R.id.password_details_password)

        passwordTextView.text = getString(R.string.secret_password)


        val sendLoginButton = findViewById<Button>(R.id.send_login_button)
        sendLoginButton.setOnClickListener { btService.write(extras?.getString(EXTRA_USERNAME)!!.toByteArray()) }

        val sendPasswordButton = findViewById<Button>(R.id.send_password_button)
        sendPasswordButton.setOnClickListener { btService.write(extras?.getString(EXTRA_PASSWORD)!!.toByteArray()) }

        val showPasswordButton = findViewById<Button>(R.id.show_password_button)
        showPasswordButton.setOnClickListener {
            passwordTextView.text = if (IsPasswordShowed) getString(R.string.secret_password) else  extras?.getString(EXTRA_PASSWORD)
            IsPasswordShowed = !IsPasswordShowed
        }

    }
}
