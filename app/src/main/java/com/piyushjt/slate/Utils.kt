package com.piyushjt.slate

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.TextView

object Utils {



    // Change visibility of views
    fun changeVisibility(views: List<View>, makeVisible: Boolean){
        if (makeVisible){
            for (view in views){
                view.visibility= View.VISIBLE
            }
        }
        else{
            for (view in views){
                view.visibility= View.GONE
            }
        }
    }



    // Vibration haptics
    fun haptic(context: Context){
        val vibrator = context.getSystemService(Vibrator::class.java)

        vibrator.vibrate(VibrationEffect.createOneShot(40, 90))

    }



    // Text Limiting
    fun textLimiter(textView: TextView, ruleView: TextView, maxChar: Int){


        // Limiting length of username
        val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxChar))
        textView.filters = filters



        // Showing the limit message
        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (s != null && s.length == maxChar) {

                    ruleView.visibility= View.VISIBLE

                }

            }
        })
    }



}