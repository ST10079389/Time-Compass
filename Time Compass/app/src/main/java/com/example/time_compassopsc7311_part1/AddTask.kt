package com.example.time_compassopsc7311_part1

import Category
import CategoryList
import Task
import TaskList
import Tasks
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.time_compassopsc7311_part1.databinding.ActivityAddTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTask : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private val taskList = mutableListOf<Task>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var popupMenu: PopupMenu
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var taskName : TextView
    private lateinit var description : TextView
    private lateinit var categoryChoice : Spinner
    private lateinit var date : TextView
    private lateinit var startText : TextView
    private lateinit var endText : TextView
    private lateinit var galleryImage : ImageView
    private lateinit var pickImageFromGalleryBtn : Button
    private lateinit var pickImageFromCameraBtn : Button
    private var imageUrl : String? = null
    private var uri: Uri? = null
    private lateinit var saveBtn : Button
    private lateinit var cameraImageUrl : Uri
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
        galleryImage.setImageURI(null)
        galleryImage.setImageURI(cameraImageUrl)
        uri = cameraImageUrl
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //declaring the variables
        taskName = binding.taskNameText
        description = binding.descrptionText
        categoryChoice = binding.categoryOption
        //description = findViewById(R.id.descriptionText)
        categoryChoice = findViewById(R.id.categoryOption)
        date = findViewById(R.id.dateText)
        startText = findViewById(R.id.startText)
        endText = findViewById(R.id.endText)
        galleryImage = findViewById(R.id.imageView)
        pickImageFromGalleryBtn = findViewById(R.id.galleryButton)
        pickImageFromCameraBtn = findViewById(R.id.cameraButton)
        saveBtn = findViewById(R.id.savebutton)
        //private lateinit var imageUrl : Uri


        //this part is for the populating spinner
        val categoryList = mutableListOf<String>()
        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        val firebaseReference = FirebaseDatabase.getInstance()
        val arrayAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            categoryList
        )
        val categoryRef =
            firebaseReference.getReference("Categories").orderByChild("userID").equalTo(userID)
        categoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (categoryShot in snapshot.children) {
                    val category = categoryShot.child("categoryName").value.toString()
                    if (category != null) {
                        categoryList.add(category)
                    }
                }

                categoryChoice.adapter = arrayAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )

        //categoryChoice.adapter = arrayAdapter
        categoryChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val categoryItem = parent?.getItemAtPosition(position) as String
                //categoryChoice.setBackgroundColor(categoryColor[position].toColorInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        date.setOnClickListener {
            datePicker()//datepicker function
        }
        startText.setOnClickListener {
            startTime()//time picker function
        }
        endText.setOnClickListener {
            endTime()//time picker function
        }
        //images from camera
        cameraImageUrl = createImageUri()
        pickImageFromCameraBtn.setOnClickListener{
            contract.launch(cameraImageUrl)
        }
        //images from gallery
       /* val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data!!.data
                binding.imageView.setImageURI(uri)
            }
        }*/

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.imageView.setImageURI(it)
            if(it != null){
              uri = it
            }
        }

        pickImageFromGalleryBtn.setOnClickListener{
        pickImage.launch("image/*")
        }



        saveBtn.setOnClickListener{
            saveTask()
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
    private fun datePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            this,
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our edit text.
                val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                date.setText(dat)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )
        datePickerDialog.setOnShowListener{
            val dateLayout = it as DatePickerDialog
            val okButton = dateLayout.getButton(DatePickerDialog.BUTTON_POSITIVE)//had to set a colour the themes in android studio wont show my buttons
            okButton.setTextColor(Color.BLACK)
        }

        datePickerDialog.show()
    }
    private fun startTime(){
        val cal = Calendar.getInstance()
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            // on below line we are passing context.
            this,
            { view, selectHour, selectMinute ->
                // on below line we are setting
                // date to our edit text.
                var selected = String.format("%02d:%02d", selectHour, selectMinute)
                startText.setText(selected)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            hourOfDay,
            minute,
            true
        )
        timePickerDialog.setOnShowListener {
            val timeLayout = it as TimePickerDialog
            val okButton = timeLayout.getButton(TimePickerDialog.BUTTON_POSITIVE)
            okButton.setTextColor(Color.BLACK)
        }
        timePickerDialog.show()
    }
    private fun endTime(){
        val cal = Calendar.getInstance()
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            // on below line we are passing context.
            this,
            { view, selectHour, selectMinute ->
                // on below line we are setting
                // date to our edit text.
                var selected = String.format("%02d:%02d", selectHour, selectMinute)
                endText.setText(selected)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            hourOfDay,
            minute,
            true
        )
        timePickerDialog.setOnShowListener {
            val timeLayout = it as TimePickerDialog
            val okButton = timeLayout.getButton(TimePickerDialog.BUTTON_POSITIVE)
            okButton.setTextColor(Color.BLACK)
        }
        timePickerDialog.show()
    }
    private fun createImageUri():Uri{
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(this,
            "com.coding.captureimage.FileProvider",
            image)
    }
   /* private fun saveImage() {
        val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
            .child(uri!!.lastPathSegment!!)
        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageUrl = urlImage.toString()
        }
        saveTask()
    }*/

    private fun saveTask(){
        val taskName = taskName.text.toString()
        val category = categoryChoice.selectedItem.toString()
        val description = description.text.toString()
        val taskDate = date.text.toString()
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val startTime = startText.text.toString()
        val endTime = endText.text.toString()
        val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTimeValue = tf.parse(startTime)
        val endTimeValue = tf.parse(endTime)
        val timeDifference = (endTimeValue.time - startTimeValue.time)
        //saveImage()
        val taskImg = imageUrl.toString()
        if(taskName.isEmpty()){
            Toast.makeText(this, "Invalid, enter task name to add a task successfully.", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty()){
            Toast.makeText(this, "Invalid, enter a description to add a task successfully.", Toast.LENGTH_SHORT).show()
        }
        else if(category.isEmpty()){
            Toast.makeText(this, "Invalid, select a category to add a task successfully.", Toast.LENGTH_SHORT).show()
        }
        else if(taskDate.isEmpty()){
            Toast.makeText(this, "Invalid, enter a task date to add a task successfully.", Toast.LENGTH_SHORT).show()
        }
        else if(startTime.isEmpty()){
            Toast.makeText(this, "Invalid, enter a start time to add a task successfully.", Toast.LENGTH_SHORT).show()
        }
        else if(endTime.isEmpty()){
            Toast.makeText(this, "Invalid, enter an end time to add a task successfully.", Toast.LENGTH_SHORT).show()
        }
        else if(taskImg.equals(null)){
            Toast.makeText(this, "Invalid, upload an image to add a task successfully.", Toast.LENGTH_SHORT).show()
        }
        else if(endTime < startTime){
            Toast.makeText(this, "Invalid, time cannot be before start time. Please enter a valid time.", Toast.LENGTH_SHORT).show()
        }
        else if(taskName.isEmpty() || description.isEmpty() || category.isEmpty() || taskDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || taskImg.equals(null)){
            Toast.makeText(this, "Invalid, enter the required details.", Toast.LENGTH_SHORT).show()
        }
        else {
            val firebaseAuth = FirebaseAuth.getInstance().currentUser
            val userID = firebaseAuth?.uid.toString()
            databaseReference = FirebaseDatabase.getInstance().getReference("Tasks")
            val taskID = databaseReference.push().key.toString()

            uri?.let{
                val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
                    .child(taskID).putFile(it).addOnSuccessListener { image->
                        image.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                            val imgUrl = url.toString()

                            val newTask = Tasks(taskID, userID, taskName, description, category, taskDate, startTime, endTime,timeDifference , imgUrl)
                            databaseReference.child(taskID).setValue(newTask)
                        }
                    }
            }
            //val newTask = Tasks(taskID,userID, taskName, description, category, taskDate, startTime, endTime, timeDifference, taskImg)
            //val newTask = Tasks(taskID, userID, taskName, description, category, taskDate, startTime, endTime,timeDifference , taskImg)
           // databaseReference.child(taskID).setValue(newTask)
            val intent = Intent(this, TaskAvailable::class.java)
            startActivity(intent)
            finish()
        }
    }
}

