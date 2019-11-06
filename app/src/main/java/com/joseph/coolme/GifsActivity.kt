package com.joseph.coolme


import androidx.appcompat.app.AppCompatActivity

import android.os.Build
import android.os.Bundle
import android.view.View

import com.joseph.coolme.R

class GifsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gifs)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }
}
