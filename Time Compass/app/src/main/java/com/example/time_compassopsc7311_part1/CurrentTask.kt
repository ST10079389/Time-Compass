package com.example.time_compassopsc7311_part1

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.time_compassopsc7311_part1.databinding.ActivityCurrentTaskBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CurrentTask : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var popupMenu: PopupMenu
    private lateinit var delete : ImageButton
    private lateinit var edit : ImageButton
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCurrentTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //view details
        val taskName = intent.getStringExtra("taskName")
        val taskDescription = intent.getStringExtra("taskDescription")
        val taskCategory = intent.getStringExtra("taskCategory")
        val taskStart = intent.getStringExtra("taskStart")
        val taskEnd = intent.getStringExtra("taskEnd")
        val taskDate =intent.getStringExtra("taskDate")
        val taskImgPath = intent.getStringExtra("taskImg")
        var taskID = intent.getStringExtra("taskID").toString()
        //val taskImg = Uri.parse(taskImgPath)


        binding.taskName.setText(taskName)
        binding.description.setText(taskDescription)
        binding.categorydisplay.setText(taskCategory)
        binding.dateDisplay.setText(taskDate)
        binding.timeDisplay.setText(taskStart + " - " + taskEnd)
        Picasso.get().load(taskImgPath).into(binding.imageView2)
        //binding.imageView2.setImageURI(taskImg)
        delete = findViewById(R.id.deleteButton)

        delete.setOnClickListener {
            val artDialogBuilder = AlertDialog.Builder(this@CurrentTask)

            artDialogBuilder.setMessage("Are you sure you want to delete this task?")
            artDialogBuilder.setTitle("Confirm")
            artDialogBuilder.setCancelable(false)

            artDialogBuilder.setPositiveButton("Yes"){_,_ ->
                deleteTask(taskID)
            }
            artDialogBuilder.setNegativeButton("No"){_,_ ->
                Toast.makeText(this, "Cancelled Delete", Toast.LENGTH_SHORT).show()
            }
            val alertDialogBox = artDialogBuilder.create()
            alertDialogBox.show()
            alertDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black))
            alertDialogBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.black))
        }



        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)

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


    private fun deleteTask(taskID: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks")
        databaseReference.child(taskID).removeValue().addOnSuccessListener {
            val intent = Intent(this, TaskAvailable::class.java)
            startActivity(intent)
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

    private fun navigateToStats() {
        // Proceed to Stats page
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile() {
        // Proceed to profile page
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }
}