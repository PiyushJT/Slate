package com.piyushjt.slate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.piyushjt.slate.databinding.ActivityAccountBinding


class Account : AppCompatActivity() {

    // LateInit
    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding= ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth= FirebaseAuth.getInstance()


        // Getting Username from main activity
        val intent = intent
        binding.username.setText(intent.getStringExtra("username"))



        // Limiting length of username
        Utils.textLimiter(binding.username, binding.usernameRule, 12)



        // Log out
        binding.logOutBtn.setOnClickListener {

            // Confirming log Out by dialog box
            val head = getString(R.string.conf_log_out)
            val logOutTxt= getString(R.string.log_out)

            Utils.showDialog(this, head, logOutTxt) { logOut() }

        }


        // Back Button
        binding.backBtn.setOnClickListener {
            finish()
        }


        // Changing Username on Save Button
        binding.saveBtn.setOnClickListener {


            // Initializing Realtime Database
            val database = Firebase.database(getString(R.string.database_url))
            val uid = auth.currentUser?.uid
            val usernameRef = database.getReference("$uid/username")


            // New username entered by user
            val username = binding.username.text.toString()


            // If the user tries to be over smart
            if(username in listOf("login", "लॉगिन", "લોગિન")){
                Toast.makeText(this, getString(R.string.invld_usnrm), Toast.LENGTH_LONG).show()
            }

            else {
                usernameRef.setValue(username)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this, getString(R.string.succ_chnged_usrnm),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

        }

    }



    // Function to log out
    private fun logOut(){
        auth.signOut()
        finish()
    }


}