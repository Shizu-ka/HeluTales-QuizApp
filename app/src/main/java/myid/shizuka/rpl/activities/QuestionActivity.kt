package myid.shizuka.rpl.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import myid.shizuka.rpl.R
import myid.shizuka.rpl.adapters.OptionAdapter
import myid.shizuka.rpl.models.Question
import myid.shizuka.rpl.models.Quiz

class QuestionActivity : AppCompatActivity() {

    var quizzes: MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? = null
    var index = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        setUpFirestore()
        setUpEventListener()
    }

    private fun setUpEventListener() {
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        btnPrevious.setOnClickListener {
            index--
            bindViews()
        }

        btnNext.setOnClickListener {
            index++
            bindViews()
        }

        btnSubmit.setOnClickListener {
            Log.d("FINALQUIZ", questions.toString())

            val intent = Intent(this, ResultActivity::class.java)
            val json = Gson().toJson(quizzes!![0])
            intent.putExtra("QUIZ", json)
            startActivity(intent)
        }
    }

    private fun setUpFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val quizTitle = intent.getStringExtra("Title")
        if (quizTitle != null) {
            firestore.collection("quizzes").whereEqualTo("title", quizTitle)
                .get()
                .addOnSuccessListener {
                    if (it != null && !it.isEmpty) {
                        quizzes = it.toObjects(Quiz::class.java)
                        questions = quizzes!![0].questions
                        bindViews()
                    }
                }
        }
    }

    private fun bindViews() {
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        btnPrevious.visibility = View.GONE
        btnSubmit.visibility = View.GONE
        btnNext.visibility = View.GONE

        if (index == 1) { //first question
            btnNext.visibility = View.VISIBLE
        } else if (index == questions!!.size) { // last question
            btnSubmit.visibility = View.VISIBLE
            btnPrevious.visibility = View.VISIBLE
        } else { // Middle
            btnPrevious.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE
        }
        val question = questions!!["question$index"]
        val description = findViewById<TextView>(R.id.description)
        val optionList = findViewById<RecyclerView>(R.id.optionList)
        question?.let {
            description.text = it.description
            val optionAdapter = OptionAdapter(this, it)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
    }
}
//    private fun bindViews() {
//        btnPrevious.visibility = View.GONE
//        btnSubmit.visibility = View.GONE
//        btnNext.visibility = View.GONE
//
//        if (index == 1) { //first question
//            btnNext.visibility = View.VISIBLE
//        } else if (index == questions!!.size) { // last question
//            btnSubmit.visibility = View.VISIBLE
//            btnPrevious.visibility = View.VISIBLE
//        } else { // Middle
//            btnPrevious.visibility = View.VISIBLE
//            btnNext.visibility = View.VISIBLE
//        }
//
//        val question = questions!!["question$index"]
//        question?.let {
//            description.text = it.description
//            val optionAdapter = OptionAdapter(this, it)
//            optionList.layoutManager = LinearLayoutManager(this)
//            optionList.adapter = optionAdapter
//            optionList.setHasFixedSize(true)
//        }
//    }
