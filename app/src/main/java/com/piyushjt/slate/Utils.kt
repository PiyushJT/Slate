package com.piyushjt.slate

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View

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



}