package com.piyushjt.projects.slate_notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.piyushjt.projects.slate_notes.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // Creating account
        binding.createAccountBtn.setOnClickListener {

            // getting user details
            val email = binding.emailRegister.text.toString()
            val username = binding.usernameRegister.text.toString()
            val password = binding.passwordRegister.text.toString()


            // checking for emptiness and nulls
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {

                    // account creation
                    MainActivity.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {

                            // success
                            if (it.isSuccessful) {

                                // getting user's id
                                val currentUser = MainActivity.auth.currentUser
                                val currentUserUid = currentUser?.uid.toString()

                                // uploading the username
                                addUsername(username, currentUserUid)

                                val intent= Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        // failure report
                        .addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                        }

            }

            // if all the details are not provided
            else {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_LONG).show()
            }
        }

    }


    // function to upload the username to realtime database
    private fun addUsername(username: String, userId: String) {

        // Get a reference to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("usernames/${userId}/username")

        // Uploading the username
        myRef.setValue(username)
    }
}