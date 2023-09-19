package activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.workinandoutapplication.R


class MainActivity : ComponentActivity() {
    val PREFS_NAME = "LoginCookiePrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)
        val endButton = findViewById<Button>(R.id.endButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val greetingText = findViewById<TextView>(R.id.greetingText)

        updateStatus()

        startButton.setOnClickListener {
            showToast("출근 버튼 누름")
            //startButton.isEnabled = false;
            //startButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            // 출근 버튼 눌렀을 때 로직
        }

        endButton.setOnClickListener {
            showToast("퇴근 버튼 누름")
            // 퇴근 버튼 눌렀을 때 로직
        }

        loginButton.setOnClickListener {
            // 로그인 버튼 눌렀을 때 로직
            startActivity(
                Intent(
                    this@MainActivity,
                    SignInActivity::class.java
                )
            )
        }

        logoutButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("id")
            editor.putBoolean("isLoggedIn", false) // 로그인 상태를 false로 설정

            editor.apply()
            updateStatus()
        }

        registerButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    SignUpActivity::class.java
                )
            )

        }
    }

    private fun updateStatus() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val greetingText = findViewById<TextView>(R.id.greetingText)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if(isLoggedIn) {
            loginButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
            registerButton.visibility = View.GONE
            val loginTextVal = sharedPreferences.getString("id", null)
            if(!loginTextVal.isNullOrEmpty()) {
                greetingText.text = buildString {
                    append(loginTextVal)
                    append(" 님 안녕하세요")
                }
                greetingText.visibility = View.VISIBLE
            } else {
                greetingText.visibility = View.GONE
            }
        } else {
            loginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
            logoutButton.visibility = View.GONE
            greetingText.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

}