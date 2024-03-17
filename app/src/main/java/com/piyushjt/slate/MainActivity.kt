package com.piyushjt.slate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.piyushjt.slate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    // LateInit
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Splash screen
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addNoteBtn.setOnClickListener {
            startActivity(Intent(this, Note::class.java))
        }



        // authentication
        auth = Firebase.auth


        // googleSignInOption (gso)
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions
                .DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        // Account Details and login
        binding.loginDetail.setOnClickListener {

            // getting sign up info for UI
            val userLoginDetails = binding.loginDetail.text.toString()



            // Open Login view or Account activity
            if (userLoginDetails == "login") {

                val signInClient = googleSignInClient.signInIntent
                launcher.launch(signInClient)

            }
            else {

                val accountActivity = Intent(this, Account::class.java)

                accountActivity.putExtra("username", userLoginDetails)
                startActivity(accountActivity)


            }

        }


    }

    override fun onResume() {
        super.onResume()
        changeUI()
    }



    // Function to change UI on basis of login
    private fun changeUI() {



        // Defining Views
        val loginInfoUI = listOf(
            binding.curvedArrowPointingToLogin,
            binding.materialCardView,
            binding.imageView2
        )
        val newNoteInfoUI = listOf(
            binding.textView,
            binding.curvedArrowPointingToAddANote,
            binding.materialCardView2,
            binding.imageView3,
            binding.addNoteBtn
        )



        val email = auth.currentUser?.email


        // If user has not logged in
        if (email.isNullOrEmpty()) {
            binding.loginDetail.text = getString(R.string.login)


            Utils.changeVisibility(loginInfoUI, true)
            Utils.changeVisibility(newNoteInfoUI, false)

        }

        // If user has logged in
        else {

            // Initializing Realtime Database
            val database = Firebase.database(getString(R.string.database_url))
            val userID = auth.currentUser?.uid
            val usernameRef = database.getReference(userID.toString()).child("username")



            // changing username
            usernameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val uName = dataSnapshot.getValue(String::class.java).toString()

                    binding.loginDetail.text = uName

                }

                override fun onCancelled(error: DatabaseError) {}
            })



            Utils.changeVisibility(newNoteInfoUI, true)
            Utils.changeVisibility(loginInfoUI, false)

        }

    }



    // Google sign in
    private val launcher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult()){
        result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            val account: GoogleSignInAccount? = task.result
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {


                        // Getting user's Email
                        val email = auth.currentUser?.email


                        // Initializing Realtime Database
                        val database = Firebase.database(getString(R.string.database_url))
                        val userID = auth.currentUser?.uid
                        val usernameRef = database.getReference(userID.toString())
                            .child("username")



                        // Getting username from database and updating in Main Activity
                        usernameRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val uName = dataSnapshot.getValue(String::class.java).toString()


                                // If username is not available  -> create username from email
                                if (uName == "null") {
                                    var name = email?.substringBefore('@').toString()


                                    name = when {
                                        name.length > 11 -> name.subSequence(0, 11).toString()
                                        else -> name
                                    }



                                    // Uploading username to database
                                    usernameRef.setValue(name)

                                }

                            }


                            override fun onCancelled(error: DatabaseError) {}
                        })

                        changeUI()
                    }
                }
        }
    }


}