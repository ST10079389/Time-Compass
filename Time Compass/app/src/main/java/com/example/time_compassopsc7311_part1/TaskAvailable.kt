package com.example.time_compassopsc7311_part1

import Task
import TaskList
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.time_compassopsc7311_part1.databinding.ActivityTaskAvailableBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskAvailable : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var popupMenu: PopupMenu
    private lateinit var listView : ListView
    private lateinit var databaseReference: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTaskAvailableBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* val taskList = TaskList.taskList.toList()
        val recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TaskAdapter(taskList)
        recyclerView.adapter = adapter*/

        //setting the display
        val taskList = mutableListOf<Task>()
        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance()
        val taskRef = databaseReference.getReference("Tasks").orderByChild("userID").equalTo(userID)
        var recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskList.clear()

        taskRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(taskShot in snapshot.children){
                    val task = taskShot.getValue(Task::class.java)
                    if(task != null){
                        taskList.add(task)
                    }
                }
                val adapter = TaskAdapter(taskList)
                recyclerView.adapter = adapter
                adapter.onItemClick = {taskList ->
                    val intent = Intent(this@TaskAvailable, CurrentTask::class.java)
                    intent.putExtra("taskName", taskList.taskName)
                    intent.putExtra("taskDescription", taskList.description)
                    intent.putExtra("taskCategory", taskList.category)
                    intent.putExtra("taskStart", taskList.startTime)
                    intent.putExtra("taskEnd", taskList.endTime)
                    intent.putExtra("taskDate", taskList.taskDate)
                    intent.putExtra("taskImg", taskList.taskImg)
                    intent.putExtra("taskID", taskList.taskID)
                    startActivity(intent)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )

        //val taskList = TaskList.taskList.toList()
        /*val recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TaskAdapter(taskList)
        recyclerView.adapter = adapter

        //making an intent to display specific task
       adapter.onItemClick = {taskList ->
           val intent = Intent(this, CurrentTask::class.java)
           intent.putExtra("taskName", taskList.taskName)
           intent.putExtra("taskDescription", taskList.description)
           intent.putExtra("taskCategory", taskList.category)
           intent.putExtra("taskStart", taskList.startTime)
           intent.putExtra("taskEnd", taskList.endTime)
           intent.putExtra("taskDate", taskList.taskDate)
           intent.putExtra("taskImg", taskList.picture.toString())
           startActivity(intent)
       }*/


        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray
        val searchbtn = binding.searchBtn
        val timer = binding.timerbtn

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)
        searchbtn.setOnClickListener {
            navigateToFilter()
        }
        timer.setOnClickListener {
            navigateToTimer()
        }

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
    private fun navigateToFilter() {
        // Proceed to Stats page
        val intent = Intent(this, FilterTasks::class.java)
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

    private fun navigateToTimer() {
        // Proceed to Timer
        val intent = Intent(this, Timer::class.java)
        startActivity(intent)
    }
}