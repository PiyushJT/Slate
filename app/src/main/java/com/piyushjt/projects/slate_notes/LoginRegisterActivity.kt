package com.piyushjt.projects.slate_notes

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.piyushjt.projects.slate_notes.databinding.ActivityLoginRegisterBinding

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initializing variable For register or login
        var RorL: Char = 'L'
        login()


        // going to login screen
        binding.downBtn.setOnClickListener {
            if (RorL == 'R') {
                animateUpOrDown(1)
                login()
                RorL = 'L'
            }
        }

        // going to register screen
        binding.upBtn.setOnClickListener {
            if (RorL == 'L') {
                animateUpOrDown(-1)
                createAccount()
                RorL = 'R'
            }
        }

    }


    // function to create account (register)
    private fun createAccount(){
        // Creating account
        binding.registerBtn.setOnClickListener {
            vibe()
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

                            val intent = Intent(this, MainActivity::class.java)
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
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // function to login
    private fun login(){
        // log in via logIn button (email and password)
        binding.loginBtn.setOnClickListener {
            vibe()
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            // checking for emptiness
            if (email.isNotEmpty() && password.isNotEmpty()) {
                MainActivity.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                        // if success
                        if (it.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }

                        // if failed
                    }.addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }


    // function to travel between login and register screen
    private fun animateUpOrDown(UorD: Int) {

        val loginScreen = binding.loginScreen
        val registerScreen = binding.registerScreen
        val googlebtn = binding.googleBtn
        val googlebtnCont = binding.googleBtnContainer

        val height = UorD * registerScreen.height.toFloat() * 0.92f


        if (UorD == -1) {

            binding.loginRegister.text= "Register"


            loginScreen.animate()
                .translationY(height)
                .setDuration(500)
                .start()
        } else {

            binding.loginRegister.text= "Login"


            loginScreen.animate()
                .translationY(0f) // Move back to original position (Y=0)
                .setDuration(500)
                .start()
        }


        registerScreen.animate()
            .translationYBy(height)
            .setDuration(500)
            .start()

        googlebtn.animate()
            .translationYBy(height)
            .setDuration(500)
            .start()

        googlebtnCont.animate()
            .translationYBy(height)
            .setDuration(500)
            .start()
    }


    // function to upload the username to realtime database
    private fun addUsername(username: String, userId: String) {

        // Get a reference to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("usernames/${userId}/username")

        // Uploading the username
        myRef.setValue(username)
    }


    // haptics feedback using vibration
    fun vibe(){
        val vibrate= getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        vibrate.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE))
    }

}