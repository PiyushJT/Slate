package com.piyushjt.projects.slate_notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piyushjt.projects.slate_notes.databinding.ActivityLoginRegisterBinding

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var RorL: Char = 'L'

        binding.downBtn.setOnClickListener {
            if (RorL == 'R') {
                animateUpOrDown(1)
                RorL = 'L'
            }
        }

        binding.upBtn.setOnClickListener {
            if (RorL == 'L') {
                animateUpOrDown(-1)
                RorL = 'R'
            }
        }


    }

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
}