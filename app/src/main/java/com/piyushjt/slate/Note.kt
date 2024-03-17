package com.piyushjt.slate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piyushjt.slate.databinding.ActivityNoteBinding

class Note : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}