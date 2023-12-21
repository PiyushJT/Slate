package com.piyushjt.projects.slate_notes

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.piyushjt.projects.slate_notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)

        binding.signUp.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
    }


    override fun onResume() {
        super.onResume()
        binding.userDetails.text = updateData()

        if(binding.userDetails.text != "Not Signed In"){
            binding.signUp.visibility= View.INVISIBLE
        }
    }

    private fun updateData(): String {
        var email= "${auth.currentUser?.email}"
        email= email.substringBeforeLast("@")
        return email
    }

}