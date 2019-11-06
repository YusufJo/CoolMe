package com.joseph.coolme


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.View
import com.facebook.stetho.Stetho
import com.joseph.coolme.model.FirebaseMemeImagesDownloader
import com.joseph.coolme.model.MemeImage
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 203
    }

    /** Start point of the application.
     *  Sets the layout of the current activity.
     *  gets the layout to be drawn beneath the status bar
     *  provides applicationContext to MemeImage through passing this activity instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        Stetho.initializeWithDefaults(this)
        layViewsUnderStatusBar()

        MemeImage.setContextObserver(this)
        FirebaseMemeImagesDownloader.setContextObserver(this)

        if (!hasSignedIn) {
            showSignInOptions()
        }
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
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
        if (hasDownloadedImages) {
            val intent = Intent(this, MemesDetailActivity::class.java)
            startActivity(intent, bundle)
        } else {
            val intent = Intent(this, DownloadMemesActivity::class.java)
            startActivity(intent, bundle)
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

    private fun showSignInOptions() {
        if (FirebaseAuth.getInstance().currentUser == null)
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(), RC_SIGN_IN
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val idpResponse = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                Toast.makeText(applicationContext, "welcome to the heaven of meme lords",Toast.LENGTH_LONG).show()
                updateSignInStatusInSharedPrefs()
                Log.d("SIGN IN TAG", "Sign in was successful")
                return
            } else {
                if (idpResponse?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    Log.d("SIGN IN TAG", "Sign in failed with no connection")
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    showSignInOptions()
                    return
                }
                if (idpResponse == null) {
                    Log.d("SIGN IN TAG", "Sign in failed with null response")
                    Toast.makeText(applicationContext, "Sign In failed", Toast.LENGTH_SHORT).show()
                    showSignInOptions()
                    return
                }
                Toast.makeText(applicationContext, "Unknown error", Toast.LENGTH_SHORT).show()
                Log.d("SIGN IN TAG", "Sign in failed with unknown error, ${idpResponse.error}")
                showSignInOptions()
            }
        }
    }

    private fun updateSignInStatusInSharedPrefs() {
        val sharedPreferences = getSharedPreferences(resources.getString(R.string.user_shared_prefrences), Context.MODE_PRIVATE)
        val sharedPreferencesEditor = sharedPreferences.edit()
        val key = resources.getString(R.string.has_previously_signed_in)
        sharedPreferencesEditor.putBoolean(key, true).apply()
    }

    private val hasDownloadedImages: Boolean
        get() = getSharedPreferences(resources.getString(R.string.user_shared_prefrences), Context.MODE_PRIVATE)
                .getBoolean(resources.getString(R.string.has_previously_downloaded_images), false)


    private val hasSignedIn: Boolean
        get() = getSharedPreferences(resources.getString(R.string.user_shared_prefrences), Context.MODE_PRIVATE)
                .getBoolean(resources.getString(R.string.has_previously_signed_in), false)

}
