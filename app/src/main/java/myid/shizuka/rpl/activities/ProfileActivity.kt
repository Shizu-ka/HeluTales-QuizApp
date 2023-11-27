package myid.shizuka.rpl.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import myid.shizuka.rpl.R
import myid.shizuka.rpl.adapters.FirebaseHelper
import myid.shizuka.rpl.adapters.ProfileAdapter
import myid.shizuka.rpl.adapters.ProfileAdapterCallback
import myid.shizuka.rpl.models.User
import myid.shizuka.rpl.utils.Drawer

class ProfileActivity : AppCompatActivity(), ProfileAdapterCallback {
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth
    private val user = Firebase.auth.currentUser
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var profileAdapter: ProfileAdapter

    var pUser = User().apply {
        setEmail(currentUser?.email.toString())
        setPassword("")
    }

    override fun onResume() {
        super.onResume()
        user?.reload()
        updateVerificationStatusUI()
    }

    private fun onVerificationComplete() {
        updateVerificationStatusUI()
    }

    private fun updateVerificationStatusUI() {
        val btnSendVerification = findViewById<Button>(R.id.btnSendVerification)
        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)
        val etStatus = findViewById<TextView>(R.id.etStatus)
        if (user?.isEmailVerified == true) {
            btnSendVerification.alpha = 0.5f
            btnSendVerification.isEnabled = false
            btnDeleteAccount.isEnabled = true
            btnDeleteAccount.alpha = 1f

            etStatus.setText("Verified")
            etStatus.setTextColor(Color.GREEN)
        } else {
            btnSendVerification.isEnabled = true
            btnSendVerification.alpha = 1f
            btnDeleteAccount.isEnabled = false
            btnDeleteAccount.alpha = 0.5f

            etStatus.setText("Unverified")
            etStatus.setTextColor(Color.RED)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user?.reload()
        setContentView(R.layout.activity_update_profile)
        setUpDrawerLayout()
        profileAdapter = ProfileAdapter(this, this)
        setUpViews()
        val btnSendVerification = findViewById<Button>(R.id.btnSendVerification)
        val btnDeleteAccount = findViewById<TextView>(R.id.btnDeleteAccount)

        btnDeleteAccount.setOnClickListener {
            profileAdapter.showPasswordPopup()
        }
        firebaseAuth = FirebaseAuth.getInstance()

        btnSendVerification.setOnClickListener{
            sendVerification()
        }
    }

    private fun setUpViews() {
        if (currentUser != null) {
            val etUEmailAddress = findViewById<EditText>(R.id.etUEmailAddress)
            etUEmailAddress.setText(currentUser.email)
            etUEmailAddress.isEnabled = false
            etUEmailAddress.isFocusable = false
            etUEmailAddress.isFocusableInTouchMode = false
        }

        val btnSendVerification = findViewById<Button>(R.id.btnSendVerification)
        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)

        if (user?.isEmailVerified == true) {
            val etStatus = findViewById<TextView>(R.id.etStatus)
            btnSendVerification.alpha = 0.5f
            btnSendVerification.isEnabled = false
            btnDeleteAccount.isEnabled = true

            etStatus.setText("Email Status : Verified")
            etStatus.setTextColor(Color.parseColor("#51FF0D"))
        } else {
            val etStatus = findViewById<TextView>(R.id.etStatus)
            btnSendVerification.isEnabled = true
            btnDeleteAccount.isEnabled = false
            btnDeleteAccount.alpha = 0.5f
            etStatus.setText("Email Status : Unverified")
            etStatus.setTextColor(Color.parseColor("#FF3131"))
        }
        val textView2 = findViewById<TextView>(R.id.textView2)
        val textView3 = findViewById<TextView>(R.id.textView3)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()
        val userResultRef = userId?.let { firestore.collection("completedQuiz").document(it) }
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        FirebaseHelper.fetchQuizCount(this) { totalQuizCount ->
            userResultRef?.get()?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val completedQuizCount = documentSnapshot.data?.size ?: 0

                    val progressPercentage = (completedQuizCount.toFloat() / totalQuizCount) * 100
                    progressBar.progress = progressPercentage.toInt()

                    val animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progressPercentage.toInt())
                    animation.duration = 3000 // Set the duration of the animation in milliseconds
                    FirebaseHelper.fetchAverageScore(this) { averageScore ->
                        textView3.text = "Average Score: $averageScore"
                        textView2.text = "Congratulations, you have done $completedQuizCount / $totalQuizCount Quiz!!"
                    }

                    animation.start()
                }
            }?.addOnFailureListener { exception ->
                Toast.makeText(this, "There is something wrong, please contact us", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var isVerificationAllowed = true
    private val verificationHandler = Handler(Looper.getMainLooper())
    private var remainingTimeInSeconds = 60
    val tempEmail = pUser.getEmail()

    private fun sendVerification() {
        if (isVerificationAllowed) {
            isVerificationAllowed = false
            user?.sendEmailVerification()?.addOnCompleteListener {
                Toast.makeText(this, "Verification email has been sent to your email", Toast.LENGTH_SHORT).show()
                onVerificationComplete()
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
        val currentPage = "profilePage"
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

//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when(menuItem.itemId) {
//                R.id.mainPage -> {
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                }
//                R.id.followUs -> {
//                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
//                    startActivity(browserIntent)
//                }
//                R.id.rateUs -> {
//                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
//                    startActivity(browserIntent)
//                }
//                R.id.logOut -> {
//                    FirebaseAuth.getInstance().signOut()
//                    intent = Intent(this, LoginActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    finish()
//                }
//            }
//            mainDrawer.closeDrawers()
//            true
//        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Drawer.setupNavigationDrawer(this, appBar, mainDrawer, navigationView,currentPage)
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

    override fun onDeleteAccountConfirmed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}