package activity

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
import network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import service.ApiService

class SignUpActivity : AppCompatActivity() {

    private val apiService = NetworkClient.createService(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val idEditText = findViewById<EditText>(R.id.idEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
       // val faceImageEditText = findViewById<EditText>(R.id.faceImageEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val backButton = findViewById<Button>(R.id.backButton)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)

        signUpButton.setOnClickListener {
            val id = idEditText.text.toString()
            val password = passwordEditText.text.toString()
            //val faceImage = faceImageEditText.text.toString()

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

                // 서버로 회원가입 요청 보내기
                sendSignUpRequest(user)
            }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun sendSignUpRequest(user: User) {
        val call = apiService.registerUser(user)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    showToast("가입 완료")
                    startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                } else {
                    val res: String = response.message()
                    showToast("가입 실패 $res")
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("완전 실패")
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
    }
}