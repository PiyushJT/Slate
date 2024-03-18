package com.piyushjt.slate

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
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
        val usernameEditText = binding.username
        val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
        usernameEditText.filters = filters



        // Changing visibility of Username rule ( < 13 )
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length == 12) {
                    binding.usernameRule.visibility = View.VISIBLE
                }
            }
        })



        // Log out
        binding.logOutBtn.setOnClickListener {

            Utils.haptic(this)
            showDialog() // Alert Dialog

        }




        // Changing Username on Save Button
        binding.saveBtn.setOnClickListener {


            // Initializing Realtime Database
            val database = Firebase.database(getString(R.string.database_url))
            val uid = auth.currentUser?.uid
            val usernameRef = database.getReference("$uid/username")


            // New username entered by user
            val username = binding.username.text.toString()


            usernameRef.setValue(username)
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.succ_chnged_usrnm),
                        Toast.LENGTH_SHORT).show()
                }



        }

    }



    // Showing alert dialog box
    private fun showDialog() {


        // Getting some strings for alert box
        val head = getString(R.string.conf_log_out)
        val posBtnTxt = getString(R.string.log_out)



        // Generating dialog box
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        // Defining views of the dialog
        val dialogTitle : TextView = dialog.findViewById(R.id.dialogTitle)
        val posBtn : Button = dialog.findViewById(R.id.posBtn)
        val cancelBtn : Button = dialog.findViewById(R.id.cancelBtn)


        // setting values of dialog box
        dialogTitle.text = head
        posBtn.text = posBtnTxt



        // cancelling log out
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }


        // Log out
        posBtn.setOnClickListener {
            auth.signOut()
            finish()
        }


        dialog.show()


    }



}