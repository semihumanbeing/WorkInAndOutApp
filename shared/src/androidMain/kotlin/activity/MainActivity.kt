package activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.example.workinandoutapplication.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dto.User
import network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import service.ApiService
import java.time.LocalDate


class MainActivity : ComponentActivity() {
    val LOGINPREFS = "LoginCookiePrefs"
    val STATUSPREFS = "StatusPrefs"
    private val apiService = NetworkClient.createService(ApiService::class.java)

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
            sendWorkInRequest()
        }

        endButton.setOnClickListener {
            sendWorkOutRequest()
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
            val loginPrefs = getSharedPreferences(LOGINPREFS, Context.MODE_PRIVATE)
            val statusPrefs = getSharedPreferences(STATUSPREFS, Context.MODE_PRIVATE)
            val loginEditor = loginPrefs.edit()
            loginEditor.remove("id")
            loginEditor.putBoolean("isLoggedIn", false) // 로그인 상태를 false로 설정
            val statusEditor = statusPrefs.edit()
            statusEditor.putBoolean("workInClicked", false)
            statusEditor.putBoolean("workOutClicked", false)
            startButton.setBackgroundResource(R.drawable.rounded_button)
            endButton.setBackgroundResource(R.drawable.rounded_button)
            loginEditor.apply()
            statusEditor.apply()
            updateStatus()
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this@MainActivity,SignUpActivity::class.java))
        }
    }

    private fun sendWorkInRequest() {
        val startButton = findViewById<Button>(R.id.startButton)
        val statusPrefs = getSharedPreferences(STATUSPREFS, Context.MODE_PRIVATE)
        val loginPrefs = getSharedPreferences(LOGINPREFS, Context.MODE_PRIVATE)
        val id = loginPrefs.getString("id", "")!!
        val isLoggedIn = loginPrefs.getBoolean("isLoggedIn", false)
        val isWorkIn = statusPrefs.getBoolean("workInClicked", false) // true이면 출근처리됨, false이면 안됨

        if(!isLoggedIn) {
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            return
        }
        if(isWorkIn) {
            startButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            showToast("이미 출근 되어 있습니다")

        } else { // 로그인 되고 출근처리 안됐으면
            startButton.setBackgroundColor(
                ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            val call = apiService.workIn(User(id, ""))
            call.enqueue(object : Callback<Map<String, Boolean>> {
                override fun onResponse(call: Call<Map<String,Boolean>>, response: Response<Map<String, Boolean>>) {
                    if(response.isSuccessful){
                        val statusEditor = statusPrefs.edit()
                        val map = response.body()
                        val result = map?.get("result")!!
                        statusEditor.putBoolean("workInClicked", result)
                        statusEditor.apply()
                        startButton.setBackgroundColor(
                            ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                        startButton.isEnabled = false
                        showToast("출근 성공")
                    }
                }

                override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                    showToast("출근 실패")
                    startButton.setBackgroundResource(R.drawable.rounded_button)
                }

            })
        }
    }
    private fun sendWorkOutRequest() {
        val endButton = findViewById<Button>(R.id.endButton)
        val statusPrefs = getSharedPreferences(STATUSPREFS, Context.MODE_PRIVATE)
        val loginPrefs = getSharedPreferences(LOGINPREFS, Context.MODE_PRIVATE)
        val id = loginPrefs.getString("id", "")!!
        val isLoggedIn = loginPrefs.getBoolean("isLoggedIn", false)
        val isWorkOut = statusPrefs.getBoolean("workOutClicked", false) // true이면 퇴근처리됨, false이면 안됨

        if(!isLoggedIn) {
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            return
        }
        if(isWorkOut) {
            endButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            showToast("이미 퇴근 되어 있습니다")

        } else { // 로그인 되고 퇴근처리 안됐으면
            val call = apiService.workOut(User(id, ""))
            call.enqueue(object : Callback<Map<String, Boolean>> {
                override fun onResponse(call: Call<Map<String,Boolean>>, response: Response<Map<String, Boolean>>) {
                    if(response.isSuccessful){
                        val statusEditor = statusPrefs.edit()
                        val map = response.body()
                        val result = map?.get("result")!!
                        statusEditor.putBoolean("workOutClicked", result)
                        statusEditor.apply()
                        showToast("퇴근 성공")
                        endButton.setBackgroundColor(
                            ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                        endButton.isEnabled = false
                    }
                }

                override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                    showToast("퇴근 실패")
                }

            })
        }
    }

    private fun updateStatus() {
        val loginPrefs = getSharedPreferences(LOGINPREFS, Context.MODE_PRIVATE)
        val startButton = findViewById<Button>(R.id.startButton)
        val endButton = findViewById<Button>(R.id.endButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val greetingText = findViewById<TextView>(R.id.greetingText)

        val isLoggedIn = loginPrefs.getBoolean("isLoggedIn", false)
        if(isLoggedIn) {
            val loginTextVal = loginPrefs.getString("id", null)
            greetingText.text = buildString {
                append(loginTextVal)
                append(" 님 안녕하세요")
            }
            greetingText.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
            registerButton.visibility = View.GONE

            if(!loginTextVal.isNullOrEmpty()) {
                // 다우오피스 출근 체크
                syncStatus(loginTextVal)
                val statusPrefs = getSharedPreferences(STATUSPREFS, Context.MODE_PRIVATE)
                val workInClicked = statusPrefs.getBoolean("workInClicked", false)
                val workOutClicked = statusPrefs.getBoolean("workOutClicked", false)

                if(workInClicked) {
                    startButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                } else {
                    startButton.setBackgroundResource(R.drawable.rounded_button)
                }

                if(workOutClicked) {
                    endButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                } else {
                    endButton.setBackgroundResource(R.drawable.rounded_button)
                }

            } else {
                greetingText.visibility = View.GONE
            }
        } else {
            startButton.isEnabled = true
            endButton.isEnabled = true
            loginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
            logoutButton.visibility = View.GONE
            greetingText.visibility = View.GONE
        }
    }

    private fun syncStatus(userId: String) {
        val sharedPreferences = getSharedPreferences(STATUSPREFS, Context.MODE_PRIVATE)
        val startButton = findViewById<Button>(R.id.startButton)
        val endButton = findViewById<Button>(R.id.endButton)

        if(userId.isEmpty()) return
        val call = apiService.syncStatus(User(userId, ""))
        call.enqueue(object : Callback<Map<String, Boolean>> {
            override fun onResponse(call: Call<Map<String,Boolean>>, response: Response<Map<String, Boolean>>) {
                if(response.isSuccessful) {
                    val map = response.body()
                    val inValue = map?.get("in")
                    val outValue = map?.get("out")

                    val editor = sharedPreferences.edit()
                    inValue?.let { editor.putBoolean("workInClicked", it) }
                    outValue?.let { editor.putBoolean("workOutClicked", it) }
                    editor.apply()

                    if(inValue == true) {
                        startButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                    } else {
                        startButton.setBackgroundResource(R.drawable.rounded_button)
                    }

                    if(outValue == true) {
                        endButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                    } else {
                        endButton.setBackgroundResource(R.drawable.rounded_button)
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                println("불러오기 실패  ${t.message}")
            }

        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

}