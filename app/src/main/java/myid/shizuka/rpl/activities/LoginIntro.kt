package myid.shizuka.rpl.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import myid.shizuka.rpl.R
import myid.shizuka.rpl.adapters.LoginIntroAdapter

class LoginIntro : BaseActivity() {
    private lateinit var adapter: LoginIntroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_intro)
        adapter = LoginIntroAdapter(this)
        adapter.checkCurrentUser()

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            adapter.redirectToLogin()
        }
    }
}

