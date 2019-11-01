package com.joseph.coolme


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.*
import android.view.View
import com.facebook.stetho.Stetho
import com.joseph.coolme.model.FirebaseMemeImagesDownloader


class MainActivity : AppCompatActivity() {


    /** Start point of the application.
     *  Sets the layout of the current activity.
     *  gets the layout to be drawn beneath the status bar
     *  provides applicationContext to MemeImage through passing this activity instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Stetho.initializeWithDefaults(this)
        layViewsUnderStatusBar()

        MemeImage.setContextObserver(this)
        FirebaseMemeImagesDownloader.setContextObserver(this)
    }


    /** Button listener for memes_section_button, when clicked, it determines whether there are saved images
     *  in the meme images directory, if image/s exist/s, MemeDetailActivity is started, else, DownloadMemeActivity is started
     *  to download Meme Images from Firebase storage.
     *
     *  @see com.joseph.coolme.R.id.memes_section_button
     *  @see com.joseph.coolme.MemeImage.memeTemplatesDirectory
     *  @see com.joseph.coolme.MemesDetailActivity
     *  @see com.joseph.coolme.DownloadMemesActivity
     */
    fun onClickStartMemesActivity(view: View) {
        if (hasDownloadedImages) {
            val intent = Intent(this, MemesDetailActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, DownloadMemesActivity::class.java)
            startActivity(intent)
        }

    }

    /** Button listener for gif_section_button, when clicked, it determines whether there are saved objects
     *  in the GIFs objects directory in the SharedPreferences, if object/s exist/s, GifsDetailActivity is started, else, DownloadGifsActivity is started
     *  to download Gifs from firebase storage
     *
     *
     */
    fun onClickStartGifsActivity(view: View) {
//        TODO("Complete what in the document and complete the document refrences")
        val intent = Intent(this, GifsActivity::class.java)
        startActivity(intent)
    }

    /** Draws the layout of this activity under the statusBar
     *  @see com.joseph.coolme.MainActivity
     *  @see com.joseph.coolme.R.layout.activity_main
     */
    private fun layViewsUnderStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private val hasDownloadedImages : Boolean
    get() = getSharedPreferences(resources.getString(R.string.user_shared_prefrences), Context.MODE_PRIVATE)
                .getBoolean(resources.getString(R.string.has_previously_downloaded_images), false)

}
