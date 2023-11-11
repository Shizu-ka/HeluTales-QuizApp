package myid.shizuka.rpl.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import myid.shizuka.rpl.R
import myid.shizuka.rpl.models.Question
import android.util.Log
import android.widget.Button
import com.google.android.material.appbar.MaterialToolbar

class OptionAdapter(val context: Context, var question: Question) :
    RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var options: List<String> = listOf(question.option1, question.option2, question.option3, question.option4)
    private var selectedOption: String? = null

    fun getSelectedOption(): String? {
        return selectedOption
    }

    fun updateQuestion(newQuestion: Question) {
        question = newQuestion
        options = listOf(newQuestion.option1, newQuestion.option2, newQuestion.option3, newQuestion.option4)
        notifyDataSetChanged()
    }

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var optionView = itemView.findViewById<TextView>(R.id.quiz_option)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.option_item, parent, false)
        return  OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.optionView.text = options[position]
        holder.itemView.setOnClickListener {
            question.userAnswer = options[position] // Update the userAnswer for the current question
            selectedOption = options[position]
            notifyDataSetChanged()
            Log.d("QuestionActivity", "Selected Option: ${options[position]}")
        }

        if (question.userAnswer == options[position]) {
            //Buat selected option background
            holder.itemView.setBackgroundResource(R.drawable.option_item_selected_bg2)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.option_item_bg)
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }
}