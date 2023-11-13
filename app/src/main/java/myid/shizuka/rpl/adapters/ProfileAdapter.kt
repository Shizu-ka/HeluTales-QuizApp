package myid.shizuka.rpl.adapters

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import myid.shizuka.rpl.R
import myid.shizuka.rpl.activities.LoginActivity

interface ProfileAdapterCallback {
    fun onDeleteAccountConfirmed()
}
class ProfileAdapter(private val context: Context, private val callback: ProfileAdapterCallback) {

    private val user = Firebase.auth.currentUser
    private val currentUser = FirebaseAuth.getInstance().currentUser
    fun showPasswordPopup() {
        val passwordDialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        passwordDialog.setTitle("Enter Password")
        val passwordInput = EditText(context)
        passwordInput.background.setColorFilter(
            ContextCompat.getColor(context, R.color.color_primary),
            PorterDuff.Mode.SRC_ATOP
        )
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
                Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmationPopup() {
        val confirmationDialog = AlertDialog.Builder(context)
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
                callback.onDeleteAccountConfirmed()
            } else {
                Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
            }
        }
    }
}