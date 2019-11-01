package com.joseph.coolme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_memes_detail.*

class MemesDetailActivity : AppCompatActivity() {
    private lateinit var currentDisplayedImage: MemeImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memes_detail)
        val imageMeme = MemeImage.loadSavedMemeImages()[0]
        currentDisplayedImage = imageMeme
        image_view_test.setImageBitmap(imageMeme.getBitmapImage)
        name_text_view.text = imageMeme.name
        category_text_view.text = imageMeme.category

    }

    fun onClickRemoveImage(view: View) {
        MemeImage.deleteMemeImage(currentDisplayedImage)
        Toast.makeText(applicationContext,"Done", Toast.LENGTH_LONG).show()
        val imageMeme = MemeImage.loadSavedMemeImages()[0]
        currentDisplayedImage = imageMeme
        image_view_test.setImageBitmap(imageMeme.getBitmapImage)
        name_text_view.text = imageMeme.name
        category_text_view.text = imageMeme.category
    }
}
