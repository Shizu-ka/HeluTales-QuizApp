package myid.shizuka.rpl.adapters

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import myid.shizuka.rpl.models.Quiz

class ResultAdapter(private val quiz: Quiz) {

    var percentage: Double = 0.0

    fun calculateScore(): String {
        var score = 0
        var jumlah = 0
        for (entry in quiz.getQuestions().entries) {
            jumlah += 10
            val question = entry.value
            Log.d("Question Debug", "Question: ${question.getDescription()}")
            Log.d("Question Debug", "Correct Answer: ${question.getAnswer()}")
            Log.d("Question Debug", "User's Answer: ${question.getUserAnswer()}")
            if (question.getAnswer() == question.getUserAnswer()) {
                score += 10
            }
        }
        // Kalkulasi Nilai
        percentage = (score.toDouble() / jumlah.toDouble()) * 100
        return "Your Score: $score/$jumlah (${percentage.toInt()}%)"
    }

    fun displayScore() {
        if (percentage > 70) {
            // Store di firestore
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val firestore = FirebaseFirestore.getInstance()
                val userResultRef = firestore.collection("completedQuiz").document(userId)

                val completedQuiz = mapOf(
                    quiz.getTitle() to mapOf(
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
}