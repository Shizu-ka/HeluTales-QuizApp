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
import myid.shizuka.rpl.activities.ResultActivity

class QuestionActivity : AppCompatActivity() {

    var quizzes: MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? = null
    var index = 1

    private var showingQuestion = true
    private lateinit var description: TextView
    private lateinit var optionAdapter: OptionAdapter

    private var userAnswers: MutableMap<String, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        setUpFirestore()
        setUpEventListener()
        //val btnNext = findViewById<Button>(R.id.btnNext)
        //btnNext.isEnabled = false

        //val optionList = findViewById<RecyclerView>(R.id.optionList)
        //optionList.setOnClickListener() {
        //    btnNext.isEnabled = true
        //}

        description = findViewById(R.id.description)
        optionAdapter = OptionAdapter(this, Question())
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
            if (showingQuestion) {
                // Question -> Materi
                showingQuestion = false
            } else {
                // Materi -> Question
                index++
                if (index > questions?.size ?: 0) {
                    index = 1 // Loop kembali ke data 1 kalau sudah sampai akhir
                }
                showingQuestion = true
            }
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
        val optionList = findViewById<RecyclerView>(R.id.optionList)
        val materiText = findViewById<TextView>(R.id.materiText)


        btnPrevious.visibility = View.GONE
        btnSubmit.visibility = View.GONE
        optionList.visibility = View.GONE
        materiText.visibility = View.GONE

        if (index == 1) { //first question
            btnNext.visibility = View.VISIBLE
        } else if (index == questions!!.size) { // last question
            if (showingQuestion){
                btnPrevious.visibility = View.VISIBLE
                btnNext.visibility = View.VISIBLE
            }
            else{
                btnSubmit.visibility = View.VISIBLE
                btnPrevious.visibility = View.VISIBLE
            }
        } else { // Middle
            btnPrevious.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE
        }

        val question = questions!!["question$index"]

        question?.let {
            if (showingQuestion) {
                description.text = it.description
                val optionAdapter = OptionAdapter(this, it)
                optionList.layoutManager = LinearLayoutManager(this)
                optionList.adapter = optionAdapter
                optionList.setHasFixedSize(true)
                optionList.visibility = View.VISIBLE
            } else {
                description.text = it.materi
                materiText.visibility = View.VISIBLE
                btnPrevious.visibility = View.GONE
            }
        }
    }
}
