package com.example.time_compassopsc7311_part1

import Points
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.time_compassopsc7311_part1.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var fireBaseAuth : FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseReference: DatabaseReference
    private lateinit var username : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.openLoginActivity.setOnClickListener(this)
        binding.buttonCreateAccount.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // Opens Login Screen
            R.id.openLoginActivity -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            // Save new user in firebase via Auth
            R.id.buttonCreateAccount -> {
                // RE for Email Auth
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

                val enteredEmail = binding.editTextEmailSignUp.text.toString()
                val enteredPassword = binding.editTextPasswordSignUp.text.toString()

                // Check if any of the fields are empty
                if(enteredEmail.isEmpty() || enteredPassword.isEmpty()){
                    Toast.makeText(this, "Enter required details", Toast.LENGTH_SHORT).show()
                }
                // Check if password is less than 6 characters
                else if(enteredPassword.length < 6) {
                    Toast.makeText(this, "Password cannot be less than 6 characters", Toast.LENGTH_SHORT).show()
                }
                // Check if email is valid
                else if (!enteredEmail.matches(emailPattern.toRegex())) {
                    Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                }
                // Check if user already exists
//                else if (user != null){
//                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
//                }
                // Create new user
                else {
                    fireBaseAuth = FirebaseAuth.getInstance()
                    fireBaseAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)

                    saveUserData(enteredEmail)

                    //Proceed to Home Screen
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
    // Function to save user data to SharedPreferences
    private fun saveUserData(email: String) {
        // Extract username
         username = email.substringBefore('@')

        sharedPreferences.edit().apply {
            putString("EMAIL", email)
            putString("USERNAME", username)
            apply()
        }
    }
}
