package myid.shizuka.rpl.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import myid.shizuka.rpl.R
import myid.shizuka.rpl.models.Quiz
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ResultActivity : AppCompatActivity() {

    lateinit var quiz: Quiz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setUpViews()
    }

    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        calculateScore()
        setAnswerView()
    }

    private fun setAnswerView() {
        val builder = StringBuilder("")
        for (entry in quiz.questions.entries) {
            val question = entry.value
            builder.append("<font color='#18206F'><b>Question: ${question.description}</b></font><br/><br/>")
            builder.append("<font color='#009688'>Answer: ${question.answer}</font><br/><br/>")
        }
        val txtAnswer = findViewById<TextView>(R.id.txtAnswer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtAnswer.text = Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_COMPACT);
        } else {
            txtAnswer.text = Html.fromHtml(builder.toString());
        }
    }

    private fun calculateScore() {
        val txtScore = findViewById<TextView>(R.id.txtScore)
        var score = 0
        var jumlah = 0
        for (entry in quiz.questions.entries) {
            jumlah += 10
            val question = entry.value
            Log.d("Question Debug", "Question: ${question.description}")
            Log.d("Question Debug", "Correct Answer: ${question.answer}")
            Log.d("Question Debug", "User's Answer: ${question.userAnswer}")
            if (question.answer == question.userAnswer) {
                score += 10
            }
        }
        // Kalkulasi Nilai
        val percentage = (score.toDouble() / jumlah.toDouble()) * 100

        txtScore.text = "Your Score: $score/$jumlah (${percentage.toInt()}%)"
        if (percentage > 70) {
            // Store di firestore
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val firestore = FirebaseFirestore.getInstance()
                val userResultRef = firestore.collection("completedQuiz").document(userId)

                val completedQuiz = mapOf(
                    quiz.title to mapOf(
                        "score" to percentage
                    )
                )

                // Set dokumen
                userResultRef.set(completedQuiz, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Firestore", "Document added/updated successfully!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding/updating document", e)
                    }
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            )
        )
    }
}
