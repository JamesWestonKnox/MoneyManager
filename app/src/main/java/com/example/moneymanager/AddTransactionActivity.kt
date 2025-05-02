package com.example.moneymanager

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
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


class AddTransactionActivity: AppCompatActivity() {

    private var transactionAmount: Double = 0.0
    private var transactionType: String = ""
    private var transactionCategory: String = ""
    private var transactionDate: String = ""
    private var transactionDescription: String? = ""
    private val pickCode = 1000
    private var transactionAttachment: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.addtransaction_page)

        //Setting up the pop up window
        val window = window
        val layoutParams = window.attributes

        layoutParams.width = (resources.displayMetrics.widthPixels * 1)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        layoutParams.y = -100

        window.attributes = layoutParams
        window.setBackgroundDrawableResource(android.R.color.transparent)


        //Retrieving the transaction amount
        val edtAmount = findViewById<TextView>(R.id.edtAmount)
        val amount = edtAmount.text.toString()
        transactionAmount = try{
            amount.toDouble()
        }
        catch (e: NumberFormatException){
            0.0
        }


        //Retrieving the transaction date
        val editTextDate = findViewById<TextView>(R.id.editTextDate)

        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    transactionDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    editTextDate.text = transactionDate
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
        transactionDescription = edtDescription.toString()

        //Retrieving the transaction attachment
        val btnAttachFile = findViewById<Button>(R.id.btnAttachFile)
        btnAttachFile.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "*/*"
            startActivityForResult(intent, pickCode)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickCode && resultCode == RESULT_OK) {
            val selectedFileUri: Uri? = data?.data
            selectedFileUri?.let { uri ->
                transactionAttachment = uri
                val fileName = getFileName(uri)
                Toast.makeText(this, "Selected file: $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileName(uri: Uri): String {

        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {

            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex >= 0) {
                    return it.getString(columnIndex)
                }
            }
        }
        return ""
    }
}






