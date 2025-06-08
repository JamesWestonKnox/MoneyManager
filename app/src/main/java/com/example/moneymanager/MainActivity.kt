/**
 * MainActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        registerButton = findViewById(R.id.btnRegister)
        loginButton = findViewById(R.id.btnLogin)


        //button functionality that takes the user to another page to create an account
        registerButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val sheetView = LayoutInflater.from(this).inflate(R.layout.register_popup, null)
            bottomSheetDialog.setContentView(sheetView)
            bottomSheetDialog.show()

            val firstNameField = sheetView.findViewById<EditText>(R.id.etFirstName)
            val surnameField = sheetView.findViewById<EditText>(R.id.etSurname)
            val emailField = sheetView.findViewById<EditText>(R.id.etEmail)
            val passwordField = sheetView.findViewById<EditText>(R.id.etPassword)
            val btnRegisterSheet = sheetView.findViewById<Button>(R.id.btnRegister)

            btnRegisterSheet.setOnClickListener {
                val firstName = firstNameField.text.toString().trim()
                val surname = surnameField.text.toString().trim()
                val email = emailField.text.toString().trim()
                val password = passwordField.text.toString().trim()

                if (firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showToast("Please fill in all fields")
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    val existingUser = userRepository.getUserByEmail(email)
                    if (existingUser != null) {
                        showToast("User already exists with this email")
                    } else {
                        val newUser = User(
                            id = System.currentTimeMillis(),
                            firstName = firstName,
                            surname = surname,
                            email = email,
                            password = password
                        )
                        val success = userRepository.insertUser(newUser)
                        if (success) {
                            runOnUiThread {
                                showToast("Registration successful!")
                                bottomSheetDialog.dismiss()
                            }
                        } else {
                                showToast("Registration failed. Try again.")
                        }

                    }
                }
            }
        }

        //Method allows user to login with their email and password
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val existingUser = userRepository.getUserByEmail(email)
                    if (existingUser != null && existingUser.password == password) {
                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putLong("USER_ID", existingUser.id)
                            apply()
                        }

                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showToast("Invalid email or password")
                    }
                }
            } else {
                showToast("Please enter a valid email and password")
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}

// ============================== End of file ==============================
