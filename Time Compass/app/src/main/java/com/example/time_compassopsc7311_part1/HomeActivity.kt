package com.example.time_compassopsc7311_part1

import DailyGoal
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max


class HomeActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var sharedPreferences: SharedPreferences
    private var username: String = "Unknown user"
    private lateinit var fireBaseAuth: FirebaseAuth
    private var minNumber: Float = 0f
    private var maxNumber: Float = 0f
    private var startTime: Long = 0
    private var totalTimeInApp: Long = 0
    private val handler = android.os.Handler()
    private val updateInterval = 1000L
    private var isAppRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize Firebase Authentication
        fireBaseAuth = FirebaseAuth.getInstance()

        // Retrieve the saved username
        username = sharedPreferences.getString("USERNAME", "Default Username") ?: "Unknown user"
        // Retrieve start time from SharedPreferences
        startTime = sharedPreferences.getLong("START_TIME_$username", System.currentTimeMillis())

        // Retrieve the time when the app was last destroyed
        val appDestroyedTime = sharedPreferences.getLong("APP_DESTROYED_TIME_$username", 0)

        // Calculate the total time spent in the app since the last destruction
        totalTimeInApp = sharedPreferences.getLong("TOTAL_TIME_IN_APP_$username", 0) + max(0, System.currentTimeMillis() - appDestroyedTime)

        // Set welcome message with username
        binding.button3.text = getString(R.string.welcome_message, username)

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)

        // Links to each page on the navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    navigateToProfile()
                    true
                }
                R.id.bell_icon -> {
                    navigateToStats()
                    true
                }
                R.id.controller_icon -> {
                    navigateToGame()
                    true
                }
                R.id.home_icon -> {
                    // Stay on page
                    true
                }
                else -> false
            }
        }
        displayMinAndMaxValues()
        updateAppUsage()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabPopupTray -> {
                popupMenu.show()
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
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToGame() {
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    fun navigateToAllCategories(view: View) {
        val intent = Intent(this, CategoryAvailable::class.java)
        startActivity(intent)
    }

    fun navigateToAllTasks(view: View) {
        val intent = Intent(this, TaskAvailable::class.java)
        startActivity(intent)
    }

    // Function to update app usage display with live count
    private fun updateAppUsage() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isAppRunning) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedTime = currentTime - startTime
                    val hours = elapsedTime / 3600000
                    val minutes = (elapsedTime % 3600000) / 60000
                    val seconds = ((elapsedTime % 3600000) % 60000) / 1000
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                    binding.appUsageTV.text = formattedTime

                    // Calculate the app usage percentage (assuming 24 hours is 100%)
                    val appUsagePercentage = (elapsedTime.toFloat() / (24 * 3600000)) * 100
                    setAppUsageProgress(appUsagePercentage)

                    // Reset min and max values if 24 hours have passed
                    if (elapsedTime >= 24 * 3600000) {
                        minNumber = 0f
                        maxNumber = 0f
                        binding.minNumTV.text = minNumber.toString()
                        binding.maxNumTV.text = maxNumber.toString()

                        // Reset app usage timer
                        binding.appUsageTV.text = "00:00:00"

                        // Reset start time
                        startTime = currentTime
                        // Update SharedPreferences
                        with(sharedPreferences.edit()) {
                            putLong("START_TIME_$username", startTime)
                            apply()
                        }

                        // Reset progress bars
                        setMinProgress(0f)
                        setMaxProgress(0f)
                        setAppUsageProgress(0f)
                    }

                    // Schedule the next update
                    handler.postDelayed(this, updateInterval)
                }
            }
        }, updateInterval)
    }

    override fun onPause() {
        super.onPause()
        isAppRunning = false
        // Calculate the total time spent in the app before pausing
        val currentTime = System.currentTimeMillis()
        totalTimeInApp += currentTime - startTime

        // Save the total time spent in the app and the start time
        with(sharedPreferences.edit()) {
            putLong("TOTAL_TIME_IN_APP_$username", totalTimeInApp)
            putLong("START_TIME_$username", startTime)
            apply()
        }

        // Remove any pending handler callbacks to pause the timer
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        isAppRunning = true
        // Retrieve the total time spent in the app and the start time
        totalTimeInApp = sharedPreferences.getLong("TOTAL_TIME_IN_APP_$username", 0)
        startTime = sharedPreferences.getLong("START_TIME_$username", System.currentTimeMillis())

        // Restart the timer
        updateAppUsage()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Save the current time when the app is destroyed
        val currentTime = System.currentTimeMillis()

        // Save the total time spent in the app and the current time
        with(sharedPreferences.edit()) {
            putLong("TOTAL_TIME_IN_APP_$username", totalTimeInApp + currentTime - startTime)
            putLong("APP_DESTROYED_TIME_$username", currentTime)
            apply()
        }
    }

    private fun displayMinAndMaxValues() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = fireBaseAuth.currentUser?.uid

        // Retrieve min and max numbers if they exist for today
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyGoalsRef = databaseReference.child("DailyGoals").orderByChild("userID").equalTo(userId)
        dailyGoalsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val dailyGoal = snapshot.getValue(DailyGoal::class.java)
                    if (dailyGoal != null && dailyGoal.currentDate == currentDate) {
                        val minValue = dailyGoal.minValue.toFloat()
                        val maxValue = dailyGoal.maxValue.toFloat()
                        binding.minNumTV.text = minValue.toString()
                        binding.maxNumTV.text = maxValue.toString()

                        // Set progress values for the progress bars
                        setMinProgress(minValue)
                        setMaxProgress(maxValue)
                        break
                    } else {
                        binding.minNumTV.text = "0"
                        binding.maxNumTV.text = "0"

                        // Set default progress values for the progress bars
                        setMinProgress(0f)
                        setMaxProgress(0f)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to retrieve daily goals.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setMinProgress(minValue: Float) {
        val minProgressBar = binding.circularProgressBarMin
        minProgressBar.progressMax = 24f // Max value is 24
        minProgressBar.progress = minValue
    }

    private fun setMaxProgress(maxValue: Float) {
        val maxProgressBar = binding.circularProgressBarMax
        maxProgressBar.progressMax = 24f // Max value is 24
        maxProgressBar.progress = maxValue
    }

    private fun setAppUsageProgress(appUsageValue: Float) {
        val appUsageProgressBar = binding.circularProgressBarUsage
        appUsageProgressBar.progressMax = 100f // Max value is 100%
        appUsageProgressBar.progress = appUsageValue
    }
}

/*Code Attribution:
    Portions of this code may have been inspired by examples from:
    1.
    Android Developers Documentation (https://developer.android.com)

    2.
    Stack Overflow Community (https://stackoverflow.com)

    3.
    GitHub Repositories (https://github.com)

    4.
    Medium Articles (https://medium.com)

    5.
    Official Android Developer Blog (https://android-developers.googleblog.com)

    6.
    Android Weekly Newsletter (https://androidweekly.net)

    7.
    W3Schools (https://www.w3schools.com)

    8.
    https://youtu.be/WqrpcWXBz14?si=7Eo-e6sk94u1YXXG from YouTube, CodingSTUFF
    https://youtube.com/@codingstuff070?si=pgM1V4flLW2EL2M7

    9.
    https://youtu.be/NMfRgOv-Xjk?si=OCd5d5X1b6-wqJ20 from YouTube, AndroidKnowledge
    https://youtube.com/@android_knowledge?si=7nCinmKIujITwzQ_

    10.
    https://youtu.be/extXn7YFjmY?si=9xcNJkpIEwgS5BZ9 from YouTube, Practical Coding
    https://youtube.com/@PracticalCoding?si=jcGjUZnKFwR6mCMK

    11.
    https://youtu.be/RGN6sCCX5J4?si=R54llbHfvuDnM2dD from YouTube, CodingSTUFF
    https://youtube.com/@codingstuff070?si=pgM1V4flLW2EL2M7

    12.
    https://youtu.be/aur1x2eJiFU?si=yjxJ6QBz5bVDeJG8 from YouTube, Donn Felker
    https://youtube.com/@donnfelkeryt?si=xNlCauq2789l6QWJ

    13.
    https://youtu.be/n8sJMXvBi0Y?si=4BqiAja1VTvaqNNI from YouTube, Donn Felker
    https://youtube.com/@donnfelkeryt?si=xNlCauq2789l6QWJ

    14.
    https://youtu.be/VcbeNZx6QWA?si=EDSKikKQZZUgNCKQ from YouTube, Donn Felker
    https://youtube.com/@donnfelkeryt?si=xNlCauq2789l6QWJ

    15.
    https://youtu.be/UbP8E6I91NA?si=EUdtayQvtyB_nrvz from YouTube, Fox Android
    https://youtube.com/@_foxandroid?si=i3uDG6mS8sGzTEgh

    16.
    https://youtu.be/goBUZ_HIKqE?si=_712PqowEM9JR1u6 from YouTube, Coding Meet
    https://youtube.com/@CodingMeet26?si=ZxvlEiPMLXRcODNo

    17.
    https://youtu.be/cZ323b1w-rc?si=cJ05qwiJnDpbKI15 from YouTube, Coding Meet
    https://youtube.com/@CodingMeet26?si=ZxvlEiPMLXRcODNo

    18.
    https://youtu.be/dxqD8FqMPRs?si=KGTU21tnaungENbP from YouTube, Coding Meet
    https://youtube.com/@CodingMeet26?si=ZxvlEiPMLXRcODNo

    19.
    https://medium.com/@meytataliti/simple-list-with-date-range-filter-19bd71761495 from Medium, Meyta Taliti
    https://medium.com/@meytataliti

    20.
    https://www.baeldung.com/kotlin/string-to-date#:~:text=2.1.,date%20format%20of%20our%20String: from Baeldung
    https://www.baeldung.com/kotlin/author/baeldung

    21.
    https://www.programiz.com/kotlin-programming/examples/milliseconds-minutes-seconds#:~:text=Minutes%20and%20Seconds-,fun%20main(args:%20Array)%20%7B%20val,remainder%20when%20divided%20by%2060. from Programiz
    https://www.programiz.com/

    22.
    https://kotlinlang.org/docs/collection-filtering.html#filter-by-predicate from Kotlin
    https://kotlinlang.org/

    23.
    https://medium.com/@austin_ng/some-useful-functions-of-list-collection-in-kotlin-a3b89c0b9cef#:~:text=1.,that%20satisfy%20the%20given%20predicate.&text=In%20the%20above%20example%2C%20the,age%20is%20greater%20than%2030. from Medium, Austin Ng
    https://medium.com/@austin_ng

    24.
    https://www.geeksforgeeks.org/datepicker-in-kotlin/ from Geeks For Geeks
    https://www.geeksforgeeks.org/

    25.
    https://youtu.be/2kD1Oj6s9HQ?si=5-63h5KDswIt-p_W from YouTube, Coding Meet
    https://youtube.com/@CodingMeet26?si=ZxvlEiPMLXRcODNo

    26.
    https://youtu.be/GmmyCOpIutA?si=vHTaSkR6geWiLVW6 from YouTube, Indently
    https://youtube.com/@Indently?si=kBLnoYcHtaOHzVJ9

    27.
    https://devendrac706.medium.com/timepickerdialog-android-studio-tutorial-time-picker-using-kotlin-in-android-studio-f9949732d62 from Medium, Devendra Chavan
    https://devendrac706.medium.com/

    28.
    https://youtu.be/j13h-0F-9Ok?si=oDQU_bwnwvnhiLd9 from YouTube, Coding Meet
    https://youtube.com/@CodingMeet26?si=ZxvlEiPMLXRcODNo

    29.
    (For mutable list in User.kt or other files)
    This part of the code was inspired by Bezkoder.com
    Uploaded by: bezkoder
    Available at: https://www.bezkoder.com/kotlin-list-mutable-list/

    30.
    (for shared preferences)
    This part of the code was inspired by a YouTube video
    Titled: SAVING DATA IN SHARED PREFERENCES - Android Fundamentals
    Uploaded by: Philipp Lackner
    Available at: https://www.youtube.com/watch?v=wtpRp2IpCSo

    31.
    (for button color change)
    This part of the code was inspired by a YouTube video
    Titled: HOW TO FIX ANDROID STUDIO BUTTON NOT CHANGING COLOR 2022 (FIXED) (TUTORIAL)
    Uploaded by: MaskedProgrammer
    Available at: https://www.youtube.com/watch?v=N9-6Feu93lc

    32.
    (for constraint layout)
    This part of the code was inspired by a YouTube video
    Titled: CONSTRAINT LAYOUT BASICS - Android Fundamentals
    Uploaded by: Philipp Lackner
    Available at: https://www.youtube.com/watch?v=VsgXFdynDuQ

    33.
    (For hex colour numbers)
    This part of the code was inspired by htmlcolorcodes.com
    Uploaded by: htmlcolorcodes.com
    Available at: https://htmlcolorcodes.com/

    34.
    (For popup menu)
    This part of the code was inspired by a YouTube video
    Titled: Popup Menu in Android using Kotlin | Kotlin | Android Studio Tutorial - Quick + Easy
    Uploaded by: Learn With Deeksha
    Available at: https://www.youtube.com/@learnwithdeeksha9996

    35.
    (Checking if val empty)
    This part of the code was inspired by a stack overflow post
    Uploaded by: Charles Durham
    Available at: https://stackoverflow.com/questions/45336954/checking-if-string-is-empty-in-kotlin

    36.
    https://developer.android.com/kotlin by Android Developers
    https://developer.android.com/

    37.
    (For Firebase Login And Registration)
    This part of the code was inspired by a YouTube video
    Uploaded by: Codes East
    Available at: https://youtu.be/QAKq8UBv4GI?si=Eu82C8cAi0cy4uzw

    38.
    (For Saving Data to Realtime Database)
    This part of my code was inspired by a YouTube video
    Uploaded by: Android Knowledge
    Available at: https://youtu.be/oGyQMBKPuNY?si=VNJzXWkKkWR8iv8Y

    39.
    (For Saving Data to Realtime Database)
    This part of my code was inspired by a YouTube video
    Uploaded by: Android Knowledge
    Available at: https://youtu.be/pLnhnHwLkYo?si=HMku0d73yCBY9-lS

    40.
    (From Realtime Database to Recyclerview)
    This part of my code was inspired by a YouTube video
    Uploaded by: Foxandroid
    Available at: https://youtu.be/qWwJLhlKM-s?si=J0nBVbvEtF-PyoOe

    41.
    (From Realtime Database to Recyclerview)
    This part of my code was inspired by a YouTube video
    Uploaded by: Foxandroid
    Available at: https://youtu.be/VVXKVFyYQdQ?si=YvDaMTlrWuEOn-Eo

    42.
    (How to save images to Firebase storage)
    This part of my code was inspired by a YouTube video
    Uploaded by: MkrDeveloper
    Available at: https://youtu.be/CWSiX_KzP4o?si=5dpYzLD6SflrRRJa

    43.
    (How to display image from firebase storage)
    This part of my code was inspired by a YouTube video
    Uploaded by: MkrDeveloper
    Available at: https://youtu.be/A2-v2VFwLY0?si=JzgQJZ2rGzzeksOW

    44.
    (Confetti library)
    This part of my code was inspired by a YouTube video
    Uploaded by: Foxandroid
    Available at: https://youtu.be/f9H0DHT2oP0?si=jaYYZ8AvsSw5020b

    45.
    (Profile Picture)
    This part of my code was inspired by a YouTube video
    Uploaded by: Foxandroid
    Available at: https://youtu.be/y4npeX35B34?si=Cryzew8PpmJtDSA4

    */