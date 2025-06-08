/**
 * AddTransactionActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.SurfaceControl
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class AddTransactionActivity: AppCompatActivity() {

    //Initiating variables for the transaction
    private var transactionAmount: Double = 0.0
    private var transactionType: String = ""
    private var transactionCategory: String = ""
    private var transactionDate: String = ""
    private var transactionDescription: String? = ""
    private val pickCode = 1000
    private var transactionAttachment: String? = ""
    private var budgets: List<Budget> = listOf()
    private val transactionRepo = TransactionRepository()
    private val budgetRepo = BudgetRepository()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.addtransaction_page)

        //Setting up the pop up window
        val window = window
        val layoutParams = window.attributes
        //Layout of the pop up
        layoutParams.width = (resources.displayMetrics.widthPixels * 1)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        layoutParams.y = -100

        window.attributes = layoutParams
        window.setBackgroundDrawableResource(android.R.color.transparent)

        //Retrieving the transaction date
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        //Allowing the user to choose a date
        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    //Setting the date format
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = formatter.format(selectedCalendar.time)
                    //Saving the date to the database and displaying it to the user
                    transactionDate = formattedDate
                    editTextDate.setText(formattedDate)

                }, year, month, day
            )
            datePickerDialog.show()
        }
        //Creating a drop down menu for categories and type of tranaction
        val sType: Spinner = findViewById(R.id.spinnerTransactionType)
        val sCategory: Spinner = findViewById(R.id.spinnerTransactionCategory)

        // Transaction Type options
        val types = listOf("Select Type", "Income", "Expense")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sType.adapter = typeAdapter

        // Income categories displayed when income selected
        val incomeCategories = listOf("Select Category", "Salary", "Investment Income", "Other Income")
        val expenseCategories = mutableListOf("Select Category")
        val userId = getUserid()

        //Getting the created category names and setting it to a list so the user can choose one
        CoroutineScope(Dispatchers.IO).launch {
            budgets = budgetRepo.getAllBudgetsByUser(userId)

            withContext(Dispatchers.Main) {
                expenseCategories.addAll(budgets.map { it.name })

                // If the type was already selected as "Expense", refresh category spinner
                if (transactionType == "Expense") {
                    val newAdapter = ArrayAdapter(this@AddTransactionActivity, android.R.layout.simple_spinner_item, expenseCategories)
                    newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sCategory.adapter = newAdapter
                }
            }
        }

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Select Category"))
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sCategory.adapter = categoryAdapter
        //Displays different options depending on the type of transaction
        sType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                transactionType = when (position) {
                    1 -> "Income"
                    2 -> "Expense"
                    else -> ""
                }
                val newCategories = when (transactionType) {
                    "Income" -> incomeCategories
                    "Expense" -> expenseCategories
                    else -> listOf("Select Category")
                }

                val newAdapter = ArrayAdapter(this@AddTransactionActivity, android.R.layout.simple_spinner_item, newCategories)
                newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sCategory.adapter = newAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                transactionType = ""
            }
        }

        sCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = sCategory.selectedItem.toString()
                transactionCategory = if (position > 0) selected else ""
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                transactionCategory = ""
            }
        }

        //Retrieving the transaction description
        val edtDescription: EditText = findViewById(R.id.edtDescription)
        transactionDescription = edtDescription.text.toString()

        //Retrieving the transaction attachment
        val btnAttachFile = findViewById<Button>(R.id.btnAttachFile)
        btnAttachFile.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "*/*"
            startActivityForResult(intent, pickCode)
        }
        //Cancel button functionality
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener{

            finish()
        }

        //Saving the transaction to the database
        val btnSaveTransaction = findViewById<Button>(R.id.btnSaveTransaction)
        btnSaveTransaction.setOnClickListener{

            val edtAmount = findViewById<EditText>(R.id.edtAmount)
            val edtDescription: EditText = findViewById(R.id.edtDescription)
            val amountText = edtAmount.text.toString()

            val amount = if (amountText.isNotEmpty()) amountText.toDoubleOrNull() ?: 0.0 else 0.0
            val description = edtDescription.text.toString()
            val type = transactionType
            val category = transactionCategory
            val date = transactionDate
            val attachment = transactionAttachment ?: ""
            //Making sure all the required fields are filled in
            if (amount <= 0.0 || type.isEmpty() || category.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Saving each transaction to the specific user logged in
            val userId = getUserid()
                //Calling the method that sends it to the database
            saveTransaction(userId, amount, type, category, date, description, attachment)
            finish()
        }}
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickCode && resultCode == RESULT_OK) {
            val selectedFileUri: Uri? = data?.data
            selectedFileUri?.let { uri ->
                val fileName = getFileName(uri)
                val file = File(getExternalFilesDir(null), fileName)

                contentResolver.openInputStream(uri)?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                transactionAttachment = file.absolutePath
                Toast.makeText(this, "Selected file: $fileName", Toast.LENGTH_SHORT).show()

            }
        }
     }

    private fun getFileName(uri: Uri): String {
        var name = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {

            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && it.moveToFirst()) {
                    name = it.getString(nameIndex)
                }
            }
        }
        if (name.isBlank()) {
            name = uri.lastPathSegment?.substringAfterLast('/') ?: "unknown_file"
        }
        return name
    }

    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }

    //Method created to save the transaction data to the database
    private fun saveTransaction(userId: Long, amount: Double, type: String, category: String, date: String, description: String, attachment: String) {

        val transaction = UserTransaction(
            amount = amount,
            date = date,
            category = category,
            type = type,
            description = description.ifEmpty { "No description" },
            attachment = attachment,
            userId = userId
        )

        CoroutineScope(Dispatchers.IO).launch{

            val success = transactionRepo.insertTransaction(transaction)

            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(this@AddTransactionActivity, "Transaction saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AddTransactionActivity, "Failed to save transaction", Toast.LENGTH_SHORT).show()
                }
            }
        }



    }
}


// ============================== End of file ==============================





