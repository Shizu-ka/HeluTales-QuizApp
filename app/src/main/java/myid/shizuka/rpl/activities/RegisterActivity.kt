package myid.shizuka.rpl.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import myid.shizuka.rpl.R
import myid.shizuka.rpl.adapters.RegisterAdapter

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerAdapter: RegisterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            signUpUser()
        }

        val btnLogin = findViewById<TextView>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerAdapter = RegisterAdapter(this) {
            navigateToMainActivity()
        }
    }


    private fun signUpUser() {
        val etEmailAddress = findViewById<EditText>(R.id.etEmailAddress)
        val email: String = etEmailAddress.text.toString()
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val password: String = etPassword.text.toString()
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val confirmPassword: String = etConfirmPassword.text.toString()

        registerAdapter.createUserWithEmailAndPassword(email, password, confirmPassword)
    }
    private fun navigateToMainActivity() {
        registerAdapter.redirect("MAIN")
        finish()
    }
}

