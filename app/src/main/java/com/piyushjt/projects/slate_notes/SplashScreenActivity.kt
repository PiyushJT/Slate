package com.piyushjt.projects.slate_notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.database.FirebaseDatabase
import pl.droidsonroids.gif.GifDrawable

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // making the animation non repeat
        val gifImageView = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.gifImageView)
        val gifDrawable = GifDrawable(resources, R.drawable.slate_animation) // Load the GIF
        gifImageView.setImageDrawable(gifDrawable)
        gifDrawable.loopCount = 1


        // Loading realtime database while splash screen is running
        Handler().postDelayed({

            if (Utils.isNetworkAvailable(this)) {

                val databaseReference = FirebaseDatabase.getInstance().reference
                val myRef = databaseReference.child("usernames")
                myRef.get().addOnSuccessListener {
                    val myData = it.value
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                    .addOnFailureListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 1500) // 1.5 sec min. delay
    }
}