import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import myid.shizuka.rpl.activities.Authentication
import myid.shizuka.rpl.activities.LoginActivity
import myid.shizuka.rpl.activities.MainActivity
import myid.shizuka.rpl.activities.RegisterActivity
import myid.shizuka.rpl.models.User

class LoginAdapter(private val context: Context, private val onSuccess: () -> Unit): Authentication() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: User? = null

    fun login(email: String, password: String) {
        user = User().apply {
            setEmail(email)
            setPassword(password)
        }

        if (user!!.getEmail().isBlank() || user!!.getPassword().isBlank()) {
            Toast.makeText(context, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (!email.endsWith("@student.ub.ac.id")) {
            Toast.makeText(context, "Only @student.ub.ac.id emails are allowed", Toast.LENGTH_SHORT).show()
            return
        }
        authUser()
    }

    override fun authUser() {
        user?.let {
            firebaseAuth.signInWithEmailAndPassword(it.getEmail(), user!!.getPassword())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        user!!.setIsLoggedIn(true)
                        if (user!!.getIsLoggedIn()) {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                            onSuccess.invoke() // Invoke the success callback
                        }
                    } else {
                        user!!.setIsLoggedIn(false)
                        if (!user!!.getIsLoggedIn()) {
                            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    fun redirectToRegister(){
        redirect("REGISTER")
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