package com.joseph.coolme

import android.graphics.BitmapFactory
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_memes_activity.*

class MemesActivity : AppCompatActivity() {


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memes_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onStart() {
        super.onStart()
        val memeImage = MemeImage.loadMemeObject()[0]
        meme_image_view.setImageBitmap(memeImage.getBitmapImage)
        meme_name_text_view.text = memeImage.name
        meme_category_text_view.text = memeImage.category
    }
}
