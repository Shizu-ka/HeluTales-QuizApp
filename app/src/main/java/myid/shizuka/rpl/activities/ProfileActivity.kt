package myid.shizuka.rpl.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
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

        val btnSendVerification = findViewById<Button>(R.id.btnSendVerification)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnDeleteAccount = findViewById<TextView>(R.id.btnDeleteAccount)

        if (user?.isEmailVerified == true) {
            val etStatus = findViewById<TextView>(R.id.etStatus)
            btnSendVerification.alpha = 0.5f
            btnSendVerification.isEnabled = false
            btnUpdate.isEnabled = true

            etStatus.setText("Email Status : Verified")
            etStatus.setTextColor(Color.parseColor("#51FF0D"))
        } else {
            val etStatus = findViewById<TextView>(R.id.etStatus)
            btnSendVerification.isEnabled = true
            btnUpdate.isEnabled = false
            btnUpdate.alpha = 0.5f
            etStatus.setText("Email Status : Unverified")
            etStatus.setTextColor(Color.parseColor("#FF3131"))
        }

        firebaseAuth = FirebaseAuth.getInstance()
        btnUpdate.setOnClickListener {
            updateUser()
        }

        btnSendVerification.setOnClickListener{
            sendVerification()
        }

        btnDeleteAccount.setOnClickListener {
            showPasswordPopup()
        }

    }
    private fun showPasswordPopup() {
        val passwordDialog = AlertDialog.Builder(this)
        passwordDialog.setTitle("Enter Password")
        val passwordInput = EditText(this)
        passwordDialog.setView(passwordInput)
        passwordDialog.setPositiveButton("Next") { dialog, _ ->
            val password = passwordInput.text.toString()
            verifyPassword(password)
            dialog.dismiss()
        }
        passwordDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        passwordDialog.show()
    }

    private fun verifyPassword(password: String) {
        val credential = EmailAuthProvider.getCredential(currentUser?.email ?: "", password)
        currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showConfirmationPopup()
            } else {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmationPopup() {
        val confirmationDialog = AlertDialog.Builder(this)
        confirmationDialog.setTitle("Are you sure?")
        confirmationDialog.setPositiveButton("Yes") { dialog, _ ->
            deleteAccount()
            dialog.dismiss()
        }
        confirmationDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        confirmationDialog.show()
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                // Navigate to the login activity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateUser() {
        val etUNewEmailAddressError = findViewById<TextView>(R.id.etUNewEmailAddressError)
        val etUNewPasswordError = findViewById<TextView>(R.id.etUNewPasswordError)
//        val email: String = etUNewEmailAddressError.text.toString()
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
        if (newEmail.isBlank()) {
            etUNewEmailAddressError.setText("Please type your old email if you doesn't want to change it")
            return
        }
        if (newPassword.isBlank()) {
            etUNewPasswordError.setText("Please type your old password if you doesn't want to change it")
            return
        }

        val credential = EmailAuthProvider.getCredential(currentUser?.email!!, oldPassword)

        // Reauthenticate the user with the old password
        var isMainOpened = false
        currentUser.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (user?.isEmailVerified == true) {
                // Update the email
                if (reauthTask.isSuccessful) {
                    currentUser.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener { updateEmailTask ->
                        if (updateEmailTask.isSuccessful) {
                            Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT)
                                .show()
                            Toast.makeText(this, "You need to verify your new email before you can use it, check your inbox", Toast.LENGTH_SHORT)
                                .show()
                            if (!isMainOpened) {
                                openMain()
                                isMainOpened = true
                            }
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
                }

                // Old password is correct, now update the password
                currentUser.updatePassword(newPassword)
                    .addOnCompleteListener { updatePasswordTask ->
                        if (updatePasswordTask.isSuccessful) {
                            if(newPassword != oldPassword) {
                                Toast.makeText(
                                    this,
                                    "Password updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (!isMainOpened) {
                                openMain()
                                isMainOpened = true
                            }
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

    private var isVerificationAllowed = true
    private val verificationHandler = Handler(Looper.getMainLooper())
    private var remainingTimeInSeconds = 60 // Initial value 60 seconds

    private fun sendVerification() {
        if (isVerificationAllowed) {
            isVerificationAllowed = false
            user?.sendEmailVerification()?.addOnCompleteListener {
                Toast.makeText(this, "Verification email has been sent to your email", Toast.LENGTH_SHORT).show()
            }

            // Enable verification after 1 minute
            verificationHandler.postDelayed({
                isVerificationAllowed = true
                remainingTimeInSeconds = 60
                val etTimer = findViewById<TextView>(R.id.etTimer)
                etTimer.setText("")
            }, 60000) // 60000 = 1 minute

            // Start countdown
            startCountdown()
        } else {
            val remainingTimeMessage = "Please wait for $remainingTimeInSeconds seconds before requesting verification again."
            Toast.makeText(this, remainingTimeMessage, Toast.LENGTH_SHORT).show()
        }
    }
    private fun startCountdown() {
        verificationHandler.post(object : Runnable {
            override fun run() {
                remainingTimeInSeconds--
                if (remainingTimeInSeconds > 0) {
                    val etTimer = findViewById<TextView>(R.id.etTimer)
                    val remainingTimeMessage = "Please wait for $remainingTimeInSeconds seconds before requesting verification again."
                    etTimer.setText(remainingTimeMessage)
                    verificationHandler.postDelayed(this, 1000) // Schedule the next update after 1 second
                }
            }
        })
    }

    private fun openMain() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    fun setUpDrawerLayout() {
        val appBar = findViewById<MaterialToolbar>(R.id.appBar)
        val mainDrawer = findViewById<DrawerLayout>(R.id.mainDrawer)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        setSupportActionBar(appBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, mainDrawer,
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.syncState()
        val menu = navigationView.menu
        val highlightedItem = menu.findItem(R.id.profilePage)
        highlightedItem.isChecked = true

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.mainPage -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.followUs -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
                    startActivity(browserIntent)
                }
                R.id.rateUs -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
                    startActivity(browserIntent)
                }
                R.id.logOut -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            mainDrawer.closeDrawers()
            true
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

}