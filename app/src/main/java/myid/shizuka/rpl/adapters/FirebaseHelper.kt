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
    }
}

