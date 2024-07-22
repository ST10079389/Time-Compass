package com.example.time_compassopsc7311_part1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.time_compassopsc7311_part1.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fireBaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize Firebase Authentication
        fireBaseAuth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val currentUser = fireBaseAuth.currentUser
        if (currentUser != null) {
            // User is already logged in
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Bindings for each Click
        binding.openSignUpActivity.setOnClickListener(this)
        binding.loginUser.setOnClickListener(this)
        binding.textViewForgotPassword.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Opens the Sign Up Activity
            R.id.openSignUpActivity -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            // Logs user in if credentials are Valid
            R.id.loginUser -> {
                // Get email and password
                val email = binding.editTextEmailLogin.text.toString()
                val password = binding.editTextPasswordLogin.text.toString()

                fireBaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            // Proceed to the Home Screen
                            saveUserData(email)
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
            // Checks if user exists in Firebase
            R.id.textViewForgotPassword -> {
                val email = binding.editTextEmailLogin.text.toString()

                if (email.isEmpty()) {
                    Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    fireBaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val signInMethods = task.result?.signInMethods ?: emptyList()
                            if (signInMethods.isNotEmpty()) {
                                // User exists
                                Toast.makeText(this, "User exists", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                // User does not exist
                                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        else {
                            // Error occurred while checking user existence
                            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    // Function to save user data to SharedPreferences
    private fun saveUserData(email: String) {
        // Extract username
        val username = email.substringBefore('@')

        sharedPreferences.edit().apply {
            putString("EMAIL", email)
            putString("USERNAME", username)
            apply()
        }
    }
}