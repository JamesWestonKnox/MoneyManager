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


class AddTransactionActivity: AppCompatActivity() {

    //Initiating variables for the transaction
    private var transactionAmount: Double = 0.0
    private var transactionType: String = ""
    private var transactionCategory: String = ""
    private var transactionDate: String = ""
    private var transactionDescription: String? = ""
    private val pickCode = 1000
    private var transactionAttachment: String? = ""


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


        //Retrieving the transaction amount
        val edtAmount = findViewById<EditText>(R.id.edtAmount)
        val amount = edtAmount.text.toString()
        transactionAmount = if (amount.isNotEmpty()){
            amount.toDoubleOrNull() ?: 0.0
        }
        else{
            0.0
        }

        //Retrieving the transaction date
        val editTextDate = findViewById<EditText>(R.id.editTextDate)

        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    transactionDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    editTextDate.setText(transactionDate)
                }, year, month, day

            )
            datePickerDialog.show()

        }

        //Creating a drop down menu for the type of transaction
        val sType: Spinner = findViewById(R.id.spinnerTransactionType)
        val types = listOf("Select transaction type", "Income", "Expense")

        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            types
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sType.adapter = typeAdapter

        //Retrieving the transaction type
        sType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                transactionType = when (position) {
                    1 -> "Income"
                    2 -> "Expense"
                    else -> ""
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
                transactionType = ""
            }
        }

        //Creating a drop down menu for the transaction categories
        val sCategory: Spinner = findViewById(R.id.spinnerTransactionCategory)

        //This list will be received from budgets page as they will be able to create more
        val categories = mutableListOf("Select Category", "Alcohol", "Vehicle", "Groceries")

        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sCategory.adapter = categoryAdapter

        //Retrieving the transaction category
        sCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                transactionCategory = if (position > 0) categories[position] else ""
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
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

        val db = AppDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch{
            db.transactionDao().insertTransaction(transaction)
            withContext(Dispatchers.Main){
                Toast.makeText(this@AddTransactionActivity, "UserTransaction saved", Toast.LENGTH_SHORT).show()
            }
        }



    }
}






