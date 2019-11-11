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


class MainActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 203
    }

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


    fun onClickStartMemesActivity(view: View) {
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
        if (hasDownloadedImages or hasRestoredBackupFiles) {
            val intent = Intent(this, MemesDetailActivity::class.java)
            startActivity(intent, bundle)
        } else {
            val intent = Intent(this, DownloadMemesActivity::class.java)
            startActivity(intent, bundle)
        }

    }


    fun onClickStartGifsActivity(view: View) {
        Toast.makeText(applicationContext,"Coming in version 2.0",Toast.LENGTH_SHORT).show()
    }


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
                Toast.makeText(applicationContext, "welcome to the heaven of meme lords", Toast.LENGTH_LONG).show()
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

    private val hasRestoredBackupFiles: Boolean
        get() = getSharedPreferences(resources.getString(R.string.user_shared_prefrences), Context.MODE_PRIVATE)
                .getBoolean(resources.getString(R.string.has_restored_backup_files), false)

}
