/**
 * AccountActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class AccountActivity : AppCompatActivity(){

    private lateinit var userRepository: UserRepository


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_page)

        val nameInput = findViewById<EditText>(R.id.txtInputName)
        val surnameInput = findViewById<EditText>(R.id.txtInputSurname)
        val emailInput = findViewById<EditText>(R.id.txtInputEmail)
        val passwordInput = findViewById<EditText>(R.id.txtInputPassword)
        val userId = getUserid()

        lifecycleScope.launch {
            val user = userRepository.getUserById(userId)
            user?.let {
                nameInput.setText(it.firstName)
                surnameInput.setText(it.surname)
                emailInput.setText(it.email)
                passwordInput.setText(it.password)
            }
        }

        //Making the back FAB button functional
        val fabBack = findViewById<FloatingActionButton>(R.id.fabBack)
        fabBack.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val btnSaveChanges = findViewById<Button>(R.id.btnSaveChanges)
        btnSaveChanges.setOnClickListener {
            // Capture updated values from the input fields
            val updatedName = nameInput.text.toString()
            val updatedSurname = surnameInput.text.toString()
            val updatedEmail = emailInput.text.toString()
            val updatedPassword = passwordInput.text.toString()

            // Save the updated data to database
            lifecycleScope.launch {
                userRepository.updateUser(
                    User(
                        id = userId,
                        firstName = updatedName,
                        surname = updatedSurname,
                        email = updatedEmail,
                        password = updatedPassword
                    )
                )
            }
            //success toast
            val message = "Changes saved successfully!"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    //retrieves userid from sharedPrefs
    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }
}

// ============================== End of file ==============================