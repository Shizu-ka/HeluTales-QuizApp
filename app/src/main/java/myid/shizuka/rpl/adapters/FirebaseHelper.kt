package myid.shizuka.rpl.adapters

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import myid.shizuka.rpl.models.Quiz

class FirebaseHelper {
    companion object {
        fun fetchQuizzes(context: Context, callback: (List<Quiz>) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("quizzes")
            collectionReference.addSnapshotListener { value, error ->
                if (value == null || error != null) {
                    Toast.makeText(context, "Please login", Toast.LENGTH_SHORT).show()
                } else {
                    val quizList = value.toObjects(Quiz::class.java)
                    callback(quizList)
                }
            }
        }
        fun fetchQuizCount(context: Context, callback: (Int) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("quizzes")
            collectionReference.get().addOnSuccessListener { querySnapshot ->
                val quizCount = querySnapshot.size()
                callback(quizCount)
            }.addOnFailureListener { exception ->
                Toast.makeText(context, "There is something wrong, please contact us", Toast.LENGTH_SHORT).show()
            }
        }
        fun fetchAverageScore(context: Context, callback: (Int) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("completedQuiz")

            collectionReference.get().addOnSuccessListener { querySnapshot ->
                var totalScore = 0
                var quizCount = 0

                for (document in querySnapshot) {
                    val score = document.getDouble("score")
                    if (score != null) {
                        totalScore += score.toInt()
                        quizCount++
                    }
                }

                val averageScore = if (quizCount != 0) totalScore / quizCount else 0
                callback(averageScore)
            }.addOnFailureListener { exception ->
                Toast.makeText(context, "There is something wrong, please contact us", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

