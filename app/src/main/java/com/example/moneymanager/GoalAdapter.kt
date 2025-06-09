/**
 * GoalAdapter.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoalAdapter( private val goals: MutableList<Goal>, private val onAddToGoalClick: (Goal, Int, Double?) -> Unit) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvGoalTitle)
        val amount: TextView = itemView.findViewById(R.id.tvGoalDescription)
        val totalSaved : TextView = itemView.findViewById(R.id.tvTotalSaved)
        val addToGoalBtn : Button = itemView.findViewById(R.id.btnAddToGoal)
        val addAmountInput : EditText = itemView.findViewById(R.id.etAddAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.name.text = goal.goalName

        holder.amount.text = goal.amount.toString()
        holder.totalSaved.text = goal.totalAmount.toString()

        holder.addToGoalBtn.setOnClickListener {
            val amountText = holder.addAmountInput.text.toString()
            val amountToAdd = amountText.toDoubleOrNull()

            if (amountToAdd == null || amountToAdd <= 0) {
                onAddToGoalClick(goal, position, null)
                return@setOnClickListener
            }

            onAddToGoalClick(goal, position, amountToAdd)
        }
    }

    override fun getItemCount(): Int = goals.size

    fun updateGoal(position: Int, updatedGoal: Goal) {
        goals[position] = updatedGoal
        notifyItemChanged(position)
    }
}