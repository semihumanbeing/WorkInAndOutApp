package activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workinandoutapplication.R
import dto.User
import network.AndroidCookieHandler
import network.NetworkClient
import okhttp3.Cookie
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import service.ApiService
import kotlin.jvm.optionals.getOrNull

class SignInActivity : AppCompatActivity() {

    private val apiService = NetworkClient.createService(ApiService::class.java)
    val PREFS_NAME = "LoginCookiePrefs"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val idEditText = findViewById<EditText>(R.id.idEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val signInButton = findViewById<Button>(R.id.signInButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val backButton = findViewById<Button>(R.id.backButton)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)

        signInButton.setOnClickListener {
            val id = idEditText.text.toString()
            val password = passwordEditText.text.toString()
            //val faceImage = faceImageEditText.text.toString()
            // 로그인 버튼 눌렀을 때 로직
            if(id.isBlank() || password.isBlank()) {
                if (id.isBlank()) {
                    errorTextView.text = "아이디를 입력하세요."
                    errorTextView.visibility = View.VISIBLE
                    idEditText.requestFocus()
                }
                if (password.isBlank()) {
                    errorTextView.text = "비밀번호를 입력하세요."
                    errorTextView.visibility = View.VISIBLE
                    passwordEditText.requestFocus()
                }
            } else {
                // User 객체 생성
                val user = User(id, password/*, faceImage*/)
                sendLogInRequest(user)
            }
        }

        signUpButton.setOnClickListener {
            // 회원가입 화면으로 이동
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        backButton.setOnClickListener {
            // 취소 버튼 눌렀을 때 로직
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun sendLogInRequest(user: User) {
        val call = apiService.loginUser(user)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    showToast("로그인 성공")
                    val cookies = response.headers().values("Set-Cookie")[0]
                    val id = cookies.substring(cookies.lastIndexOf("LOGINCOOKIE=")+12, cookies.indexOf(";"))
                    // 쿠키를 SharedPreferences에 저장
                    val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("id", id)
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                } else {
                    val res: String = response.message()
                    showToast("로그인 실패 $res")
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("완전 실패")
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(this@SignInActivity, message, Toast.LENGTH_SHORT).show()
    }

}