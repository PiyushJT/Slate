package com.piyushjt.projects.slate_notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.piyushjt.projects.slate_notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference

    companion object {
        lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // Going to register activity by signUp button
        binding.signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        // signing out the user
        binding.signOut.setOnClickListener {
            auth.signOut()
            getUsername()
        }
    }

    override fun onResume() {
        super.onResume()
        getUsername()
    }

    private fun getUsername() {

        // initializing realtime database
        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentUserUid = currentUser?.uid.toString()

        // checking for sign in
        if (currentUser != null) {

            // retrieving username from realtime database
            currentUser.let {
                // defining path to username
                val noteReference = databaseReference.child("usernames").child(currentUserUid).child("username")

                noteReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        // uploading username to screen
                        val username = snapshot.getValue(String::class.java)
                        binding.userDetails.text = username
                        visibilitySignUpSignOut()
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        // if user have not signed in
        } else {
            binding.userDetails.text = ""
            visibilitySignUpSignOut()
        }
    }

    // function to change visibility of signIn and signOut buttons
    private fun visibilitySignUpSignOut() {
        if (binding.userDetails.text != "") {
            binding.signUp.visibility = View.INVISIBLE
            binding.signOut.visibility = View.VISIBLE
        } else {
            binding.signUp.visibility = View.VISIBLE
            binding.signOut.visibility = View.INVISIBLE
        }
    }
}