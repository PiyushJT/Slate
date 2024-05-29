package com.piyushjt.slate

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
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
    fun haptic(context: Context) {
        val vibrator = context.getSystemService(Vibrator::class.java)

        vibrator.vibrate(VibrationEffect.createOneShot(65, 90))

        Log.d("TAG", "Haptic delivered") // For testing in emulator
    }



    // Text Limiting
    fun textLimiter(textView: TextView, ruleView: TextView, maxChar: Int) {


        // Limiting length of username
        val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxChar))
        textView.filters = filters



        // Showing the limit message
        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (s != null && s.length == maxChar) {

                    ruleView.visibility = View.VISIBLE

                }

            }
        })
    }



    // Showing alert dialog box
    fun showDialog(context: Context, head: String, posBtnTxt: String, posFun: () -> Unit) {

        haptic(context)



        // Generating dialog box
        val dialog = Dialog(context)
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



        // cancel button
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }


        // positive button
        posBtn.setOnClickListener {
            posFun()
            dialog.dismiss()
        }

        dialog.show()

    }



    // Change background tint of views
    fun changeBgTint(views: List<View>, color: String) {

        for (view in views) {
            view.setBackgroundTintList(
                ColorStateList.valueOf(
                    Color.parseColor(color)
                )
            )
        }
    }


    // Generating random color
    fun getRandomColor() : String {

        val colors = mapOf(
            1 to "#FF9E9E", // Pink
            2 to "#91F48F", // Green
            3 to "#FFF599", // Yellow
            4 to "#9EFFFF", // Aqua
            5 to "#B69CFF"  // Purple
        )

        return colors[(1..5).random()]!!

    }


}