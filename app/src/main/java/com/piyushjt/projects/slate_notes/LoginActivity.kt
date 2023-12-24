package com.piyushjt.projects.slate_notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.piyushjt.projects.slate_notes.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // going to register activity
        binding.registerTV.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        // log in via logIn button
        binding.loginBtn.setOnClickListener {
            val email= binding.emailLogin.text.toString()
            val password= binding.passwordLogin.text.toString()

            // checking for emptiness
            if (email.isNotEmpty() && password.isNotEmpty()){
                MainActivity.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{

                    // if success
                    if(it.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }

                    // if failed
                }.addOnFailureListener{
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}