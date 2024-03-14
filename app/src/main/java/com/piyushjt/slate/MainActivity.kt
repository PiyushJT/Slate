package com.piyushjt.slate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.piyushjt.slate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    // LateInit
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Splash screen
        Thread.sleep(1000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // authentication
        auth = Firebase.auth


        // googleSignInOption (gso)
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions
                .DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.client_id))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        // Account Details and login
        binding.loginDetail.setOnClickListener {

            // getting sign up info for UI
            var userLoginDetails = binding.loginDetail.text.toString()



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



        // If user has logged in
        }
        else {

            // setting username   --> needs to be changed to realtime database (upload)
            var name = email.substringBefore('@')

            name =
                if (name.length > 12) {
                    name.subSequence(0, 12).toString()
                } else {
                    name
                }


            binding.loginDetail.text = name


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
                        changeUI()
                    }
                }


        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }


}