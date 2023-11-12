package myid.shizuka.rpl.adapters

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val userResultRef = userId?.let { firestore.collection("completedQuiz").document(it) }

            userResultRef?.get()?.addOnSuccessListener { documentSnapshot ->
                var totalScore = 0
                var quizCount = 0

                if (documentSnapshot.exists()) {
                    val userQuizData = documentSnapshot.data

                    if (userQuizData != null) {
                        for (quizTitle in userQuizData.keys) {
                            val quizData = userQuizData[quizTitle] as? Map<*, *>
                            val score = quizData?.get("score") as? Double
                            Toast.makeText(context, "There is $score", Toast.LENGTH_SHORT).show()

                            if (score != null) {
                                totalScore += score.toInt()
                                quizCount++
                            }
                        }
                    }
                }

                val averageScore = if (quizCount != 0) totalScore / quizCount else 0
                callback(averageScore)
            }?.addOnFailureListener { exception ->
                Toast.makeText(context, "Error code $exception, please contact us", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

