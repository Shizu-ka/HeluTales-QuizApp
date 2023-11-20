package myid.shizuka.rpl.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import myid.shizuka.rpl.R
import myid.shizuka.rpl.adapters.OptionAdapter
import myid.shizuka.rpl.models.Question
import myid.shizuka.rpl.models.Quiz
import myid.shizuka.rpl.activities.ResultActivity
import myid.shizuka.rpl.adapters.QuestionAdapter

class QuestionActivity : AppCompatActivity() {
    private var quizzes: MutableList<Quiz>? = null
    private var questions: MutableMap<String, Question>? = null
    private var index = 1
    private var beforeIndex = 1
    private var showingQuestion = true
    private lateinit var btnNext: Button
    private lateinit var description: TextView
    private lateinit var optionAdapter: OptionAdapter
    private var selectedOption: String? = null
    private var selectedOptionBefore: String? = null
    private lateinit var questionAdapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        questionAdapter = QuestionAdapter(this)
        val quizTitle = intent.getStringExtra("Title")
        if (quizTitle != null) {
            questionAdapter.setUpFirestore(quizTitle) { quizzes ->
                this.quizzes = quizzes.toMutableList()
                this.questions = quizzes[0].questions
                bindViews()
            }
        }

        optionAdapter = OptionAdapter(this, Question())
        btnNext = findViewById(R.id.btnNext)
        description = findViewById(R.id.description)
        setUpEventListener()
    }

    private fun setUpEventListener() {
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)


        btnNext.setOnClickListener {
            if (showingQuestion) {
                // Question -> Materi
                selectedOption = optionAdapter.getSelectedOption()
                if (selectedOption == selectedOptionBefore) {
                    selectedOption = null
                } else {
                    selectedOptionBefore = selectedOption
                }
                if (selectedOption == null) {
                    Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // You can do something with the selected option, for example:
                Log.d("USER_ANSWER", "User selected option: $selectedOption")
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

    private fun bindViews() {
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val optionList = findViewById<RecyclerView>(R.id.optionList)
        val materiText = findViewById<TextView>(R.id.materiText)

        btnSubmit.visibility = View.GONE
        optionList.visibility = View.GONE
        materiText.visibility = View.GONE

        if (index == 1) { //first question
            btnNext.visibility = View.VISIBLE
        } else if (index == questions!!.size) { // last question
            if (showingQuestion) {
                btnNext.visibility = View.VISIBLE
            } else {
                btnNext.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
            }
        } else { // Middle
            btnNext.visibility = View.VISIBLE
        }

        val question = questions!!["question$index"]

        question?.let {
            if (showingQuestion) {
                description.text = it.description

                // Use the same instance of optionAdapter
                optionAdapter.updateQuestion(it)

                optionList.layoutManager = LinearLayoutManager(this)
                optionList.adapter = optionAdapter
                optionList.setHasFixedSize(true)
                optionList.visibility = View.VISIBLE
                selectedOption = null
            } else {
                description.text = it.materi
                materiText.visibility = View.VISIBLE

                selectedOption = null
            }
        }
    }
}