package com.piyushjt.projects.slate_notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.piyushjt.projects.slate_notes.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // going to loginActivity
        binding.loginTV.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


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



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.googleBtn.setOnClickListener {
            val username = binding.usernameRegister.text.toString()
            if (username.isNotEmpty()) {
                googleSignInClient.signOut()
                startActivityForResult(googleSignInClient.signInIntent, 108)

            }else{
                Toast.makeText(this, "Please provide a username", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 108 && resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        MainActivity.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // getting user's id
                    val currentUser = MainActivity.auth.currentUser
                    val currentUserUid = currentUser?.uid.toString()

                    // uploading the username
                    val username = binding.usernameRegister.text.toString()
                    addUsername(username, currentUserUid)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
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