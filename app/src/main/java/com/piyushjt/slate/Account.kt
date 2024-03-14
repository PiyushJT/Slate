package com.piyushjt.slate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piyushjt.slate.databinding.ActivityAccountBinding


class Account : AppCompatActivity() {

    // LateInit
    private lateinit var binding: ActivityAccountBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding= ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Getting Username from main activity
        val intent = intent
        binding.username.setText(intent.getStringExtra("username"))



    }
}