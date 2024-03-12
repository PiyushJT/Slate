package com.piyushjt.slate

import android.view.View

object Utils {

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

}