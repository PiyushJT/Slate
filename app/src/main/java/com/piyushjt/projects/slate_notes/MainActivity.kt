package com.piyushjt.projects.slate_notes

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

        visibilitySignUpSignOut()

        // Going to register activity by signUp button
        binding.signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        // signing out the user
        binding.signOut.setOnClickListener {
            auth.signOut()
            binding.userDetails.text = updateData()
            visibilitySignUpSignOut()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.userDetails.text = updateData()
        visibilitySignUpSignOut()
    }


    // function to change name of user in main activity
    private fun updateData(): String {
        var email= "${auth.currentUser?.email}"
        email= email.substringBeforeLast("@")
        return email
    }


    // function to change visibility of signIn and signOut btns
    private fun visibilitySignUpSignOut(){
        if (binding.userDetails.text != "null") {
            binding.signUp.visibility = View.INVISIBLE
            binding.signOut.visibility = View.VISIBLE
        }
        else{
            binding.signUp.visibility= View.VISIBLE
            binding.signOut.visibility= View.INVISIBLE
        }
    }

}