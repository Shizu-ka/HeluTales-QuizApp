package myid.shizuka.rpl.activities

import LoginAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import myid.shizuka.rpl.R

class LoginActivity : AppCompatActivity() {

    private lateinit var loginAdapter: LoginAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginAdapter = LoginAdapter(this) {
            navigateToMainActivity()
        }
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            performLogin()
        }

        val btnSignUp = findViewById<TextView>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            loginAdapter.redirectToRegister()
            finish()
        }
    }

    private fun performLogin() {
        val etEmailAddress = findViewById<EditText>(R.id.etEmailAddress)
        val email: String = etEmailAddress.text.toString()
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val password: String = etPassword.text.toString()

        loginAdapter.login(email, password)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


//    @IgnoreExtraProperties
//    data class User(val email: String? = null, val password: String? = null) {
//    }
//    fun writeNewUser(email: String, password: String) {
//        val databaseR = Firebase.database.reference
//       val userId = databaseR.child("users").push().key ?: ""
//
//        val user = User(email, password)
//
//       databaseR.child("users").child(userId).setValue(user)
////        database.child("users").child(email).setValue(user)
////        database.child("password").child(password).setValue(password)
//    }
}