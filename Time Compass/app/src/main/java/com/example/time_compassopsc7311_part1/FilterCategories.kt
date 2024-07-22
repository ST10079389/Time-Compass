package com.example.time_compassopsc7311_part1

import Task
import TaskList.taskList
import Tasks
import android.app.DatePickerDialog
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
import com.example.time_compassopsc7311_part1.databinding.ActivityFilterCategoriesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FilterCategories : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityFilterCategoriesBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var searchBtn: Button
    private lateinit var categoryChoice: Spinner
    private lateinit var startDate: TextView
    private lateinit var endDate: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray
        searchBtn = binding.savebutton
        categoryChoice = binding.categoryOption

        // Setting up the dropdown menu
        val categoryList = mutableListOf<String>()
        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        val firebaseReference = FirebaseDatabase.getInstance()
        val categoryRef = firebaseReference.getReference("Categories").orderByChild("userID").equalTo(userID)
        categoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (categoryShot in snapshot.children) {
                    val category = categoryShot.child("categoryName").value.toString()
                    if (category != null) {
                        categoryList.add(category)
                    }
                }
                val arrayAdapter = ArrayAdapter<String>(
                    this@FilterCategories,
                    android.R.layout.simple_list_item_1,
                    categoryList
                )
                categoryChoice.adapter = arrayAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        categoryChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val categoryItem = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

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
            if (startDate.text.isEmpty() || endDate.text.isEmpty()) {
                Toast.makeText(this, "Please enter all required details.", Toast.LENGTH_SHORT).show()
            } else {
                totalHoursForCategory()
            }
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
                val intent = Intent(this, AddTask::class.java)
                startActivity(intent)
                return true
            }
            R.id.addCategory -> {
                val intent = Intent(this, AddCategory::class.java)
                startActivity(intent)
                return true
            }
            else -> return false
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    private fun navigateToStats() {
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

    private fun totalHoursForCategory() {

        val category = categoryChoice.selectedItem.toString()
        val startDateInput = getDateInMillis(startDate.text.toString())
        val endDateInput = getDateInMillis(endDate.text.toString())

        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        val tasksReference = FirebaseDatabase.getInstance().getReference("Tasks").orderByChild("userID").equalTo(userID)

        tasksReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalHours = 0.0

                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Tasks::class.java)
                    if (task != null && task.category == category) {
                        val taskDateMillis = getDateInMillis(task.taskDate ?: "")
                        if (taskDateMillis in startDateInput..endDateInput) {
                            val startTimeMillis = getTimeInMillis(task.startTime ?: "")
                            val endTimeMillis = getTimeInMillis(task.endTime ?: "")
                            val timeDifferenceHours = (endTimeMillis - startTimeMillis) / (1000 * 60 * 60).toDouble()
                            totalHours += timeDifferenceHours
                        }
                    }
                }

                val formattedStartDate = startDate.text.toString()

                val formattedEndDate = endDate.text.toString()

                binding.displayTotalCategoryHours.text = String.format(
                    Locale.getDefault(),
                    "The total hours spent on category %s between %s and %s was %.2f hour/s.",
                    category,
                    formattedStartDate,
                    formattedEndDate,
                    totalHours
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FilterCategories, "Failed to retrieve the requested information.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Helper functions
    fun getDateInMillis(dateString: String): Long {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return format.parse(dateString)?.time ?: 0L
    }

    fun getTimeInMillis(timeString: String): Long {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.parse(timeString)?.time ?: 0L
    }
}
