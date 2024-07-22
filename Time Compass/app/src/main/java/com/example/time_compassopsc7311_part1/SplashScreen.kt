package com.example.time_compassopsc7311_part1

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.time_compassopsc7311_part1.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        animateLottieAndText()

        // Delay and start main activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish splash activity
        }, 3000) // 3 seconds delay
    }

    private fun animateLottieAndText() {
        val lottieAnimationView: LottieAnimationView = binding.lottie
        val textView: View = binding.appName

        // Start Lottie Animation
        lottieAnimationView.playAnimation()

        // Animate Lottie View
        ObjectAnimator.ofFloat(lottieAnimationView, "translationY", -200f).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        // Animate Text View
        ObjectAnimator.ofFloat(textView, "translationY", -100f).apply {
            duration = 2000
            startDelay = 1000 // Delay for better visual effect
            interpolator = BounceInterpolator()
            start()
        }
    }
}
