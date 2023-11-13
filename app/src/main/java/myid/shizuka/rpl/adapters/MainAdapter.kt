package myid.shizuka.rpl.adapters

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import myid.shizuka.rpl.models.Quiz

class MainAdapter(private val context: Context, private val onDataFetched: (List<Quiz>) -> Unit) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun fetchDataFromFirestore() {
        firestore.collection("quizzes")
            .get()
            .addOnSuccessListener { result ->
                val quizList = mutableListOf<Quiz>()
                for (document in result) {
                    val quiz = document.toObject(Quiz::class.java)
                    quizList.add(quiz)
                }
                onDataFetched.invoke(quizList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
