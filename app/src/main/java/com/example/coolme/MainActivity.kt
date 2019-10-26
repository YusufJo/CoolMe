package com.example.coolme

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layViewsUnderStatusBar()

    }

    override fun onStart() {
        super.onStart()
        MemeImage.filesDir = filesDir
        MemeImage.writeImageDirectoryOfMemes()
        MemeImage.writeObjectDirectoryOfMemes()
        if (MemeImage.objectsDirectory.listFiles()?.isEmpty()!!)
            MemeImage.createMemeImage(applicationContext, "حاولت اعمل حاجه صح", "الباشا تلميذ", R.drawable.basha_telmeez)
    }

    fun onClickStartMemesActivity(view: View) {
        val intent = Intent(this, MemesActivity::class.java)
        startActivity(intent)
    }

    fun onClickStartGifsActivity(view: View) {
        val intent = Intent(this, GifsActivity::class.java)
        startActivity(intent)
    }


    private fun layViewsUnderStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }


}
