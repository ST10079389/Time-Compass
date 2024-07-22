package com.example.time_compassopsc7311_part1

import Category
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.time_compassopsc7311_part1.databinding.ActivityAddCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AddCategory : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var categoryName : TextView
    private lateinit var colorOptn : Spinner
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseReference: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //select color functionality
        colorOptn = findViewById(R.id.colorOption)
        categoryName = findViewById(R.id.categoryNameText)
        colorOptn.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val colourSelected = colorOptn.selectedItem.toString()
                colorChange(colourSelected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        //save Category
        binding.saveButton.setOnClickListener {
            saveCategory()
        }

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // Initialize PopupMenu
        fabPopupTray.setOnClickListener(this)
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener(this) // Set the listener

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    // Proceed to Profile page
                    navigateToProfile()
                    true
                }
                R.id.bell_icon -> {
                    // Proceed to notification page
                    navigateToStats()
                    true
                }
                R.id.home_icon -> {
                    // Proceed to home page
                    navigateToHome()
                    true
                }
                R.id.controller_icon -> {
                    // Proceed to game page
                    navigateToGame()
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabPopupTray -> {
                popupMenu.show()
            }
        }
    }

    // Menu items for Floating Action Button (plus sign)
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addTask -> {
                // Proceed to add a task
                val intent = Intent(this, AddTask::class.java)
                startActivity(intent)
                return true
            }
            R.id.addCategory -> {
                // Proceed to add a category
                val intent = Intent(this, AddCategory::class.java)
                startActivity(intent)
                return true
            }
            else -> return false
        }
    }

    // Methods to navigate to different pages
    private fun navigateToStats() {
        // Proceed to Stats page
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        // Proceed to home page
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToGame() {
        // Proceed to game page
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile() {
        // Proceed to profile page
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }
    private fun colorChange(x:String){
        if (x.equals("Dark Blue")){
            categoryName.setTextColor(Color.parseColor("#00003f"))
        }else if (x.equals("Light Blue")){
            categoryName.setTextColor(Color.parseColor("#00ADB5"))
        }else if (x.equals("Grey")){
            categoryName.setTextColor(Color.parseColor("#808080"))
        }else if (x.equals("Orange")){
            categoryName.setTextColor(Color.parseColor("#F8B400"))
        }else if (x.equals("Purple")){
            categoryName.setTextColor(Color.parseColor("#7209B7"))
        }else if (x.equals("Black")){
            categoryName.setTextColor(Color.parseColor("#000000"))
        }else if (x.equals("White")){
            categoryName.setTextColor(Color.parseColor("#FF000000"))
        }else if(x.equals("Select Color")){
            categoryName.setTextColor(Color.parseColor("#FF000000"))
        }
    }
    private fun saveCategory(){
        val categoryName = binding.categoryNameText.text.toString()
        val categoryColor = colorOptn.selectedItem.toString()
        if(categoryName.isEmpty()){
            Toast.makeText(this, "Invalid, enter a category name.", Toast.LENGTH_SHORT).show()
        }
        else if(categoryColor.isEmpty() || categoryColor.equals("Select Color")){
            Toast.makeText(this, "Invalid, select a color for the category.", Toast.LENGTH_SHORT).show()
        }
        else if(categoryName.isEmpty() || categoryColor.isEmpty() || categoryColor.equals("Select Color")){
            Toast.makeText(this, "Invalid, enter the required details.", Toast.LENGTH_SHORT).show()
        }
        else {
            val firebaseAuth = FirebaseAuth.getInstance().currentUser
            val userID = firebaseAuth?.uid.toString()
            databaseReference = FirebaseDatabase.getInstance().getReference("Categories")
            val categoryID = databaseReference.push().key.toString()
            val newCategory = Category(categoryID, userID, categoryName, categoryColor)
            databaseReference.child(categoryID).setValue(newCategory)
            //CategoryList.categoryList.add(newCategory)
            val intent = Intent(this, CategoryAvailable::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun updatePoints() {
        var currentPoints = 0
        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        firebaseReference = FirebaseDatabase.getInstance()
        val pointRef =
            firebaseReference.getReference("Points").orderByChild("userID").equalTo(userID).get()
                .addOnSuccessListener {
                    currentPoints = it.value as Int + 50
                }
        val updatedatabase = FirebaseDatabase.getInstance().getReference("Points")
        updatedatabase.setValue(currentPoints)
    }
}