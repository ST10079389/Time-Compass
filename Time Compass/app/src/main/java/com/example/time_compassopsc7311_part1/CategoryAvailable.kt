package com.example.time_compassopsc7311_part1

import Category
import CategoryList
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.time_compassopsc7311_part1.databinding.ActivityCategoryAvailableBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityCurrentTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryAvailable : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener  {

    private lateinit var popupMenu: PopupMenu
    private lateinit var databaseReference: FirebaseDatabase
    //private lateinit var recyclerView: RecyclerView
    //private lateinit var categoryList: MutableList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCategoryAvailableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val categoryList = CategoryList.categoryList.toList()
        val categoryList = mutableListOf<Category>()
        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance()
        categoryList.clear()
        val categoryRef = databaseReference.getReference("Categories").orderByChild("userID").equalTo(userID)
        var recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(categoryShot in snapshot.children){
                    val category = categoryShot.getValue(Category::class.java)
                    if(category != null){
                        categoryList.add(category)
                    }
                }

                val adapter = CategoryAdapter(categoryList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )


       // getCategoryData()


        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray
        val searchButton = binding.searchBtn

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)
        searchButton.setOnClickListener {
            navigateToFilterCategories()
        }


        // Links to each page on the navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    navigateToProfile()
                    finish()
                    true
                }
                R.id.bell_icon -> {
                    navigateToStats()
                    finish()
                    true
                }
                R.id.controller_icon -> {
                    navigateToGame()
                    finish()
                    true
                }
                R.id.home_icon -> {
                    navigateToHome()
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    /*private fun getCategoryData() {
        val categoryList = mutableListOf<Category>()
        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance()
        val categoryRef = databaseReference.getReference("Categories").orderByChild("userID").equalTo(userID)

        categoryRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(categoryShot in snapshot.children){
                    val category = categoryShot.getValue(Category::class.java)
                    if(category != null){
                        categoryList.add(category)
                    }
                }
                val adapter = CategoryAdapter(categoryList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )
    }*/

    // show pop up menu onClick
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
    private fun navigateToProfile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    private fun navigateToStats() {
        // Proceed to Stats page
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToGame() {
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        // Proceed to home page
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToFilterCategories() {
        // Proceed to home page
        val intent = Intent(this, FilterCategories::class.java)
        startActivity(intent)
    }
}