package myid.shizuka.rpl.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import myid.shizuka.rpl.R

class LoginIntro : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_intro)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null ) {
            Toast.makeText(this, "You are already logged in!", Toast.LENGTH_SHORT).show()
            redirect("MAIN")
        }
        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            redirect("LOGIN")
        }

    }

    private fun redirect(name:String) {
        val intent = when(name) {
            "LOGIN" -> Intent(this, LoginActivity::class.java)
            "MAIN" -> Intent(this, MainActivity::class.java)
            else -> throw Exception("no path exist")
        }
        startActivity(intent)
        finish()
    }

}

