package com.example.coolme

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URI

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layViewsUnderStatusBar()

    }

    override fun onStart() {
        super.onStart()
        MemeImage.filesDir = filesDir
        MemeImage.writeObjectDirectoryOfMemes()
        MemeImage.writeImageDirectoryOfMemes()
        if (MemeImage.objectsDirectory.listFiles()?.isEmpty()!!) {
            val imageUri = Uri.parse("android.resource://com.example.coolme/drawable/abo_elaraby_ya_bta3_neswan").toString()
            MemeImage.createMemeImage(applicationContext, "يا بتاع نسوان", "ابو العربي", imageUri)

            val imageUri2 = Uri.parse("android.resource://com.example.coolme/drawable/lemby_bent_8elsa").toString()
            MemeImage.createMemeImage(applicationContext, "البنت الغلسه", "الليمبي", imageUri2)


        }

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
