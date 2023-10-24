package myid.shizuka.rpl.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import myid.shizuka.rpl.R

class ProfileActivity : AppCompatActivity() {
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var firebaseAuth: FirebaseAuth
    val user = Firebase.auth.currentUser
    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        setUpDrawerLayout()
        if (currentUser != null) {
            val etUEmailAddress = findViewById<EditText>(R.id.etUEmailAddress)
            etUEmailAddress.setText(currentUser.email)
            etUEmailAddress.isEnabled = false
            etUEmailAddress.isFocusable = false
            etUEmailAddress.isFocusableInTouchMode = false
        }
        firebaseAuth = FirebaseAuth.getInstance()
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            updateUser()
        }

//        val btnLogin = findViewById<TextView>(R.id.btnLogin)
//        btnLogin.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

    private fun updateUser() {
        val etUEmailAddress = findViewById<EditText>(R.id.etUEmailAddress)
        val email: String = etUEmailAddress.text.toString()
        val etUNewEmailAddress = findViewById<EditText>(R.id.etUNewEmailAddress)
        val newEmail: String = etUNewEmailAddress.text.toString()
        val etUOldPassword = findViewById<EditText>(R.id.etUOldPassword)
        val oldPassword: String = etUOldPassword.text.toString()
        val etUNewPassword = findViewById<EditText>(R.id.etUNewPassword)
        val newPassword: String = etUNewPassword.text.toString()

        if (oldPassword.isBlank()) {
            Toast.makeText(this, "Email or Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(currentUser?.email!!, oldPassword)

        // Reauthenticate the user with the old password
        currentUser.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (user?.isEmailVerified == true) {
                // Update the email
                if (reauthTask.isSuccessful) {
                    currentUser.updateEmail(newEmail).addOnCompleteListener { updateEmailTask ->
                        if (updateEmailTask.isSuccessful) {
                            Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val emailErrorMessage =
                                updateEmailTask.exception?.message ?: "Failed to update email"
                            Toast.makeText(this, emailErrorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        // Email update failed
                        val errorMessage = when (exception) {
                            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                            is FirebaseAuthUserCollisionException -> "An account with this email already exists"
                            else -> "Failed to update email: ${exception.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please verify your email address", Toast.LENGTH_SHORT)
                        .show()
                    user.sendEmailVerification().addOnCompleteListener {
                        Toast.makeText(this, "Please verify your email address", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                // Old password is correct, now update the password
                currentUser.updatePassword(newPassword)
                    .addOnCompleteListener { updatePasswordTask ->
                        if (updatePasswordTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val passwordErrorMessage =
                                updatePasswordTask.exception?.message ?: "Failed to update password"
                            Toast.makeText(this, passwordErrorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        // Password update failed
                        val errorMessage = when (exception) {
                            // Handle specific password validation errors if needed
                            is FirebaseAuthWeakPasswordException -> "Password should be at least 6 characters long"
                            else -> "Failed to update password: ${exception.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    fun setUpDrawerLayout() {
        val appBar = findViewById<MaterialToolbar>(R.id.appBar)
        val mainDrawer = findViewById<DrawerLayout>(R.id.mainDrawer)
        setSupportActionBar(appBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mainDrawer,
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}