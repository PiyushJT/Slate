package com.piyushjt.slate

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.piyushjt.slate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash screen
        Thread.sleep(1000)
        installSplashScreen()


        var binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // getting sign up info for UI
        var userLoginDetails = binding.loginDetail.text.toString()


        // Changing UI according to sign up details
        if (userLoginDetails == "login"){
            val loginInfoUI= listOf<View>(
                binding.curvedArrowPointingToLogin,
                binding.materialCardView,
                binding.imageView2
            )

            Utils.changeVisibility(loginInfoUI, true)
        }
        else{
            val newNoteInfoUI = listOf<View>(
                binding.textView,
                binding.curvedArrowPointingToAddANote,
                binding.materialCardView2,
                binding.imageView3,
                binding.addNoteBtn
            )

            Utils.changeVisibility(newNoteInfoUI, true)
        }


    }
}