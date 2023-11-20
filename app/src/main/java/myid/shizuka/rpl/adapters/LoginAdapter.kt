import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import myid.shizuka.rpl.models.User

class LoginAdapter(private val context: Context, private val onSuccess: () -> Unit) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String) {
        val user = User().apply {
            this.email = email
            this.password = password
        }

        if (user.email.isBlank() || user.password.isBlank()) {
            Toast.makeText(context, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (!email.endsWith("@student.ub.ac.id")) {
            Toast.makeText(context, "Only @student.ub.ac.id emails are allowed", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    onSuccess.invoke() // Invoke the success callback
                } else {
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
