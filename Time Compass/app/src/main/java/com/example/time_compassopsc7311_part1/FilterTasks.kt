package com.example.time_compassopsc7311_part1

import CategoryList
import Task
import TaskList
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.time_compassopsc7311_part1.databinding.ActivityFilterTasksBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FilterTasks : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityFilterTasksBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var searchBtn : Button
    private lateinit var startDate : TextView
    private lateinit var endDate : TextView
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: FilterTasksAdapter
    private lateinit var databaseReference: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = FilterTasksAdapter(emptyList()) // Initialize with empty list
        tasksRecyclerView.adapter = taskAdapter

        //for category spinner
        val categoryName = CategoryList.categoryList.map { it.categoryName }.toTypedArray()

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray
        searchBtn = binding.savebutton

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)
        startDate = binding.startDate
        endDate = binding.endDate
        startDate.setOnClickListener(this)
        endDate.setOnClickListener(this)
        searchBtn.setOnClickListener {
            filterTask()
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
            R.id.startDate -> {
                datePicker(startDate)
            }
            R.id.endDate -> {
                datePicker(endDate)
            }
        }
    }

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
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun datePicker(textView: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth/${monthOfYear + 1}/$year"
                textView.text = date
            },
            year,
            month,
            day
        )
        datePickerDialog.setOnShowListener {
            val dateLayout = it as DatePickerDialog
            val okButton = dateLayout.getButton(DatePickerDialog.BUTTON_POSITIVE)
            okButton.setTextColor(Color.BLACK)
        }

        datePickerDialog.show()
    }

    private fun filterTask() {
        val startTime = startDate.text.toString()
        val endTime = endDate.text.toString()

        // Check if start date and end date are not empty
        if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
            val startDateMillis = getDateInMillis(startTime)
            val endDateMillis = getDateInMillis(endTime)

            // Check if start date is before end date
            if (startDateMillis <= endDateMillis) {
                //setting the display
                val taskList = mutableListOf<Task>()
                val firebaseAuth = FirebaseAuth.getInstance().currentUser
                val userID = firebaseAuth?.uid.toString()
                databaseReference = FirebaseDatabase.getInstance()
                taskList.clear()
                val taskRef = databaseReference.getReference("Tasks").orderByChild("userID").equalTo(userID)

                taskRef.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(taskShot in snapshot.children){
                            val task = taskShot.getValue(Task::class.java)
                            if(task != null){
                                taskList.add(task)
                            }
                        }
                        //var recyclerView: RecyclerView = findViewById(R.id.taskRecyclerView)
                        //recyclerView.layoutManager = LinearLayoutManager(this@FilterTasks)
                        //val adapter = TaskAdapter(taskList)
                       // recyclerView.adapter = adapter
                        val filteredTasks = taskList.filter { task ->
                            val taskDateMillis = getDateInMillis(task.taskDate)
                            taskDateMillis in startDateMillis..endDateMillis
                        }

                        // Update RecyclerView with filtered tasks
                        taskAdapter.updateFilteredTasks(filteredTasks)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
                )
            } else {
                showToast("Start date cannot be after end date")
            }
        } else {
            showToast("Please select start and end dates")
        }
    }

    private fun getDateInMillis(dateString: String): Long {
        val pattern = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            val date = sdf.parse(dateString)
            date?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}