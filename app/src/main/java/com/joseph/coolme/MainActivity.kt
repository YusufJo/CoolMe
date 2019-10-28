package com.joseph.coolme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.random.Random

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


    }

    override fun onResume() {
        super.onResume()
        if (MemeImage.objectsDirectory.listFiles()?.isEmpty()!!) {
            MemeImage.downloadDefaultMemesFromFirebase()
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
