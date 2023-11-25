package myid.shizuka.rpl.adapters

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import myid.shizuka.rpl.activities.Authentication
import myid.shizuka.rpl.activities.LoginActivity
import myid.shizuka.rpl.activities.MainActivity

class LoginIntroAdapter(private val context: Context): Authentication() {
    private val auth = FirebaseAuth.getInstance()

    fun checkCurrentUser() {
        auth()
    }

    fun redirectToLogin() {
        redirect("LOGIN")
    }

    private fun redirect(destination: String) {
        val intent = when (destination) {
            "MAIN" -> Intent(context, MainActivity::class.java)
            "LOGIN" -> Intent(context, LoginActivity::class.java)
            else -> throw IllegalArgumentException("Invalid destination: $destination")
        }
        context.startActivity(intent)
    }

    override fun auth() {
        if (auth.currentUser != null) {
            Toast.makeText(context, "You are already logged in!", Toast.LENGTH_SHORT).show()
            redirect("MAIN")
        }
    }
}