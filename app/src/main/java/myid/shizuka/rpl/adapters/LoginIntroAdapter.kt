package myid.shizuka.rpl.adapters

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import myid.shizuka.rpl.activities.Authentication
import myid.shizuka.rpl.activities.LoginActivity
import myid.shizuka.rpl.activities.MainActivity
import myid.shizuka.rpl.models.User

class LoginIntroAdapter(private val context: Context): Authentication() {
    private val auth = FirebaseAuth.getInstance()

    fun checkCurrentUser() {
        auth()
    }

    fun redirectToLogin() {
        redirect("LOGIN")
    }

    private fun redirect(destination: String) {
        // Perform the necessary redirection logic here
        // For example, you can use Intent to start a new activity
        val intent = when (destination) {
            "MAIN" -> Intent(context, MainActivity::class.java)
            "LOGIN" -> Intent(context, LoginActivity::class.java)
            else -> throw IllegalArgumentException("Invalid destination: $destination")
        }
        context.startActivity(intent)
    }

    override fun auth() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val user = User()
            user.setEmail(currentUser.email.toString())
            user.setIsLoggedIn(true)
            if (user.getEmail().isNotBlank() && user.getIsLoggedIn()) {
                Toast.makeText(context, "You are already logged in!", Toast.LENGTH_SHORT).show()
                redirect("MAIN")
            }
        }
    }
}