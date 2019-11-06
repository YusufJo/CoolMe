package com.joseph.coolme

import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import com.joseph.coolme.model.DownloadObserver
import com.joseph.coolme.model.FirebaseMemeImagesDownloader
import com.joseph.coolme.model.MemeImage
import kotlinx.android.synthetic.main.download_meme_layout.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.properties.Delegates

class DownloadMemesActivity : AppCompatActivity(), DownloadObserver {
    private var objectsSavedCounter = 0

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_memes)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

//        MemeImage.setDownloadObserver(this)
        FirebaseMemeImagesDownloader.setContextObserver(this)
        FirebaseMemeImagesDownloader.setDownloadObserver(this)

    }

    override fun onResume() {
        super.onResume()
        download_button.setOnClickListener {
            runOnUiThread {
                download_button.isEnabled = false
                downlading_spinner.visibility = View.VISIBLE
                download_button.setBackgroundResource(R.drawable.download_button_deactivated)

                runBlocking {
                    launch {
                        File(MemeImage.memeTemplatesDirectory).listFiles()?.forEach { it.delete() }
                        getSharedPreferences(resources.getString(R.string.meme_images_shared_prefrences_key), Context.MODE_PRIVATE).edit().clear().apply()
                    }
                }.invokeOnCompletion {
                    FirebaseMemeImagesDownloader.firebaseMemesDir.child("7920923873988338478.PNG").downloadUrl.addOnSuccessListener {
                        FirebaseMemeImagesDownloader.downloadDefaultMemesFromFirebase()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }


    private var downloadCompleted: Boolean by Delegates.observable(false) { _, _, _ ->
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
        Toast.makeText(applicationContext, "اشطا $objectsSavedCounter ميم تمب نزلوا ", Toast.LENGTH_LONG).show()
        val intent = Intent(this, MemesDetailActivity::class.java)
        startActivity(intent,bundle)
        finish()
    }

    override fun updateDownloadObserver(currentSavedObjects: Int) {
        objectsSavedCounter = currentSavedObjects
        if (objectsSavedCounter == FirebaseMemeImagesDownloader.filesFromFirebaseCount)
            downloadCompleted = true
    }

}
