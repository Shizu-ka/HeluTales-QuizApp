package myid.shizuka.rpl.adapters

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import myid.shizuka.rpl.activities.LoginActivity
import myid.shizuka.rpl.activities.MainActivity
import myid.shizuka.rpl.activities.RegisterActivity
import myid.shizuka.rpl.models.User

class RegisterAdapter(private val context: Context, private val onSuccess: () -> Unit): Authentication() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: User? = null

    fun createUser(email: String, password: String, confirmPassword: String) {
        user = User().apply {
            setEmail(email)
            setPassword(password)
        }
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(context, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (!email.endsWith("@student.ub.ac.id")) {
            Toast.makeText(context, "Only @student.ub.ac.id emails are allowed", Toast.LENGTH_SHORT).show()
            return
        }
        authUser()
    }

    override fun authUser() {
        user?.let{
            firebaseAuth.createUserWithEmailAndPassword(it.getEmail(), user!!.getPassword())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val databaseR = Firebase.database.reference
                        val userId = databaseR.child("users").push().key ?: ""
                        databaseR.child("users").child(userId).setValue(user)
                        user!!.setIsSuccessful(true)
                        if (user!!.getIsSuccessful()) {
                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                            onSuccess.invoke() // Invoke the success callback

                        }
                    } else {
                        user!!.setIsSuccessful(false)
                        if (!user!!.getIsSuccessful()) {
                            Toast.makeText(context, "Error Creating User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    override fun redirect(destination: String) {
        val intent = when (destination) {
            "MAIN" -> Intent(context, MainActivity::class.java)
            "LOGIN" -> Intent(context, LoginActivity::class.java)
            "REGISTER" -> Intent(context, RegisterActivity::class.java)
            else -> throw IllegalArgumentException("Invalid destination: $destination")
        }
        context.startActivity(intent)
    }
}