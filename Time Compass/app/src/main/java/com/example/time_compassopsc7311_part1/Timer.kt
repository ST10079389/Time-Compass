package com.example.time_compassopsc7311_part1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.time_compassopsc7311_part1.databinding.ActivityTimerBinding
import com.google.android.material.button.MaterialButton

class Timer : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var popupMenu: PopupMenu
    private lateinit var handler: Handler
    private lateinit var binding: ActivityTimerBinding

    private var millisecondTime: Long = 0L
    private var startTime: Long = 0L
    private var timeBuff: Long = 0L
    private var updateTime: Long = 0L
    private var seconds: Int = 0
    private var minutes: Int = 0
    private var milliSeconds: Int = 0
    private var isRunning = false

    private val runnable = object : Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000).toInt()
            minutes = seconds / 60
            seconds = seconds % 60
            milliSeconds = (updateTime % 1000).toInt()

            binding.textView.text = String.format("%02d:%02d:%02d", minutes, seconds, milliSeconds / 10)
            handler.postDelayed(this, 50)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, binding.fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        binding.fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)

        binding.startStop.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        binding.reset.setOnClickListener {
            resetTimer()
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    navigateToProfile()
                    true
                }
                R.id.bell_icon -> {
                    navigateToStats()
                    true
                }
                R.id.home_icon -> {
                    navigateToHome()
                    true
                }
                R.id.controller_icon -> {
                    navigateToGame()
                    true
                }
                else -> false
            }
        }
    }

    private fun startTimer() {
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)
        binding.reset.isEnabled = false
        binding.startStop.text = getString(R.string.stop)
        binding.startStop.icon = getDrawable(R.drawable.pause)
        isRunning = true
    }

    private fun stopTimer() {
        timeBuff += millisecondTime
        handler.removeCallbacks(runnable)
        binding.reset.isEnabled = true
        binding.startStop.text = getString(R.string.start)
        binding.startStop.icon = getDrawable(R.drawable.playbtn)
        isRunning = false
    }

    private fun resetTimer() {
        millisecondTime = 0L
        startTime = 0L
        timeBuff = 0L
        updateTime = 0L
        seconds = 0
        minutes = 0
        milliSeconds = 0
        binding.textView.text = "00:00:00"
        binding.reset.isEnabled = false
        binding.startStop.text = getString(R.string.start)
        binding.startStop.icon = getDrawable(R.drawable.play_pause_icon)
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

    // Methods to navigate to different pages
    private fun navigateToStats() {
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToFilter() {
        val intent = Intent(this, FilterTasks::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToGame() {
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }
}
