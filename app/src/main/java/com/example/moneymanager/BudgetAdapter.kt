package com.example.moneymanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager


class BudgetAdapter(
    private val context: Context,
    private val budgets : List<Budget>,
    private var allTransactions : List<UserTransaction>

): RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLimit: TextView = view.findViewById(R.id.tv_limit)
        val tvSpent: TextView = view.findViewById(R.id.tvSpentSmall)
        val tvRemaining: TextView = view.findViewById(R.id.tvRemainingSmall)
        val progressBarSmall: CircularProgressIndicator = view.findViewById(R.id.progressBarSmall)
        val buttonExpand: Button = view.findViewById(R.id.button)
        val expandedLayout: View = view.findViewById(R.id.expandedLayout)
        val progressBarBig: CircularProgressIndicator = view.findViewById(R.id.progressBarBig)
        val tvLimitExpanded: TextView = view.findViewById(R.id.tv_limit2)
        val rvTransaction: RecyclerView = view.findViewById(R.id.rvTransact)
        val tvPercentage: TextView = view.findViewById(R.id.tvPercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.budget_item, parent, false)
        return BudgetViewHolder(view)
    }

    override fun getItemCount(): Int = budgets.size

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]


        holder.tvLimit.text = budget.name
        holder.tvSpent.text = "Spent: R${budget.budgetAmount}"
        holder.tvRemaining.text = "Remaining: R${budget.budgetMaxLimit - budget.budgetAmount}"
        holder.tvLimitExpanded.text = "Limit: R${budget.budgetMaxLimit}"



        holder.buttonExpand.setOnClickListener {
            if (holder.expandedLayout.isGone) {
                holder.expandedLayout.visibility = View.VISIBLE
                holder.buttonExpand.text = "Collapse"
                holder.progressBarSmall.visibility = View.INVISIBLE
            } else {
                holder.expandedLayout.visibility = View.GONE
                holder.buttonExpand.text = "Expand"
                holder.progressBarSmall.visibility = View.VISIBLE
            }
        }
        val matchingTransactions = allTransactions
            .filter { it.category == budget.name }
            .take(3)
        val TransactionsFromBudget = allTransactions
            .filter { it.category == budget.name }

        val totalSpent = TransactionsFromBudget.sumOf { it.amount }
        budget.budgetAmount = totalSpent
        holder.tvRemaining.text = "Remaining: R${budget.budgetMaxLimit - budget.budgetAmount}"
        holder.tvSpent.text = "Spent: R${totalSpent}"
        val progress =
            ((budget.budgetAmount.toFloat() / budget.budgetMaxLimit.toFloat()) * 100).toInt()
        holder.progressBarSmall.setProgressCompat(progress, true)
        holder.progressBarBig.setProgressCompat(progress, true)
        holder.tvPercentage.text = "${progress}%"
        holder.rvTransaction.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TransactionAdapter(context, matchingTransactions)
        }
        holder.rvTransaction.isNestedScrollingEnabled = true
        holder.rvTransaction.setHasFixedSize(true)
    }

}


