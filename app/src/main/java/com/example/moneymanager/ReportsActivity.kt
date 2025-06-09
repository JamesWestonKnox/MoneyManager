/**
 * ReportsActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reports_page)

        lifecycleScope.launch {
            val (startDate, endDate) = getCurrentMonthDateRange()
            val repo = TransactionRepository()
            val userId = getUserid()
            val expenseTotalsByCategory = repo.getExpenseTotalsByCategory(userId, startDate, endDate)
            val pieEntries = expenseTotalsByCategory.map { (category, total) ->
                PieEntry(total.toFloat(), category)
            }
            withContext(Dispatchers.Main) {
                setupPieChart(pieEntries, "Expenses\nLast 30 Days")
            }
        }

        val spinner = findViewById<Spinner>(R.id.spinner_time_period)
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPeriod = parent.getItemAtPosition(position).toString()
                lifecycleScope.launch {
                    val (startDate, endDate) = getDateRangeForBarChart(selectedPeriod)
                    val transactionRepo = TransactionRepository()
                    val budgetRepo = BudgetRepository()
                    val userId = getUserid()

                    val expenseTotals = transactionRepo.getExpenseTotalsByCategory(userId, startDate, endDate)
                    val budgetMax = budgetRepo.getBudgetMaxByCategory(userId)

                        setupBarChart(expenseTotals, budgetMax)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        //Making navBar functional
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_reports

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_reports -> {
                    true
                }
                R.id.nav_budgets -> {

                    startActivity(Intent(this, BudgetsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }
                R.id.nav_transactions -> {

                    startActivity(Intent(this, TransactionsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }

                R.id.nav_home -> {

                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }

                R.id.nav_goals -> {

                    startActivity(Intent(this, GoalsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }


                else -> false

            }

        }

    }
    private fun getDateRangeForBarChart(option: String): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return when (option) {
            "This Month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = dateFormat.format(calendar.time)
                Pair(start, end)
            }

            "Last Month" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = dateFormat.format(calendar.time)
                Pair(start, end)
            }

            "Last 3 Months" -> {
                calendar.add(Calendar.MONTH, -2)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)

                calendar.add(Calendar.MONTH, 2)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = dateFormat.format(calendar.time)
                Pair(start, end)
            }

            else -> getDateRangeForBarChart("This Month")
        }
    }
    fun getCurrentMonthDateRange(): Pair<String, String>{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        return Pair(startDate, endDate)
    }

    private fun setupPieChart(entries: List<PieEntry>, centerText: String) {
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                return value.toInt().toString()
            }
        }
        dataSet.setDrawValues(true)

        val data = PieData(dataSet)

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = true
        pieChart.setUsePercentValues(false)
        pieChart.setEntryLabelTextSize(0f)
        pieChart.setDrawEntryLabels(false)

        val legend = pieChart.legend
        legend.isWordWrapEnabled = true
        legend.textSize = 12f
        legend.xEntrySpace = 8f
        legend.yEntrySpace = 4f
        legend.formSize = 12f
        legend.formToTextSpace = 6f
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        pieChart.setExtraOffsets(10f, 10f, 10f, 20f)

        pieChart.centerText = centerText
        pieChart.setCenterTextSize(12f)
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)

        pieChart.animateY(1000)
        pieChart.invalidate()
    }




    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }

    private fun setupBarChart(expenseTotals: Map<String, Double>, budgetMax: Map<String, Double>) {
        val barChart = findViewById<BarChart>(R.id.barChart)

        val categories = expenseTotals.keys.union(budgetMax.keys).toList()
        val spentEntries = ArrayList<BarEntry>()
        val goalEntries = ArrayList<BarEntry>()

        categories.forEachIndexed { index, category ->
            val spent = expenseTotals[category] ?: 0.0
            val max = budgetMax[category] ?: 0.0

            spentEntries.add(BarEntry(index.toFloat(), spent.toFloat()))
            goalEntries.add(BarEntry(index.toFloat(), max.toFloat()))
        }

        val spentDataSet = BarDataSet(spentEntries, "Amount Spent")
        spentDataSet.color = Color.parseColor("#4CAF50")

        val goalDataSet = BarDataSet(goalEntries, "Max Limit")
        goalDataSet.color = Color.parseColor("#FF9800")

        val data = BarData(spentDataSet, goalDataSet)
        data.barWidth = 0.4f

        barChart.data = data


        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(categories)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setDrawGridLines(false)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM


        xAxis.axisMinimum = 0f
        val groupCount = categories.size
        val groupSpace = 0.2f
        val barSpace = 0.02f
        xAxis.setCenterAxisLabels(true)

        data.barWidth = 0.38f

        xAxis.axisMaximum = 0f + barChart.barData.getGroupWidth(groupSpace, barSpace) * groupCount

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false

        barChart.setExtraOffsets(10f, 10f, 15f, 10f)

        barChart.setFitBars(true)

        barChart.groupBars(0f, groupSpace, barSpace)

        barChart.legend.isWordWrapEnabled = true

        barChart.invalidate()
    }

}

// ============================== End of file ==============================