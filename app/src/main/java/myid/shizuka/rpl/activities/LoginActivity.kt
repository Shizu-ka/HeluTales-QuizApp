package myid.shizuka.rpl.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import myid.shizuka.rpl.R

class LoginActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            login()
        }

        val btnSignUp = findViewById<TextView>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        val etEmailAddress = findViewById<EditText>(R.id.etEmailAddress)
        val email: String = etEmailAddress.text.toString()
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val password: String = etPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
//        database.child("users").child(email).child("email").setValue(email)
//        database.child("users").child(password).child("password").setValue(password)
        writeNewUser(email,password)
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
    @IgnoreExtraProperties
    data class User(val email: String? = null, val password: String? = null) {
    }
    fun writeNewUser(email: String, password: String) {
        val databaseR = Firebase.database.reference
        val userId = databaseR.child("users").push().key ?: ""

        val user = User(email, password)

        databaseR.child("users").child(userId).setValue(user)
//        database.child("users").child(email).setValue(user)
//        database.child("password").child(password).setValue(password)
    }
}