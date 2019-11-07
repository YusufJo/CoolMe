package com.joseph.coolme

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.joseph.coolme.model.DownloadObserver
import com.joseph.coolme.model.FirebaseMemeImagesDownloader
import com.joseph.coolme.model.MemeImage
import kotlinx.android.synthetic.main.activity_memes_detail.*
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
        setUpDownloadButton()
        setUpRestoreBackupButton()
    }

    private fun setUpRestoreBackupButton() {
        restore_backup_button.setOnClickListener {
            runOnUiThread {
                restore_backup_button.isEnabled = false
                downlading_spinner.visibility = View.VISIBLE
                restore_backup_button.setTextColor(Color.BLACK)
                restore_backup_button.setBackgroundResource(R.drawable.button_deactivated)
            }
                checkReadExternalStoragePermission()
        }
    }

    private fun checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "Checking for backup files", Toast.LENGTH_SHORT).show()
            checkBackUpFiles()
        } else
            requestReadStoragePermission()
    }

    private fun requestReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val alertDialogBuilder: AlertDialog.Builder? = this.let {
                AlertDialog.Builder(it)
                        .setTitle("Permission is needed")
                        .setMessage("The application needs to access internal storage to check for any backup files")
                        .apply {
                            setPositiveButton("Okay", DialogInterface.OnClickListener { _, _ -> promptForReadStoragePermission() })
                            setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, _ ->
                                dialog.dismiss()
                                restore_backup_button.isEnabled = true
                                restore_backup_button.setTextColor(Color.WHITE)
                                downlading_spinner.visibility = View.GONE
                                restore_backup_button.setBackgroundResource(R.drawable.backup_button_active)
                            })
                        }
            }
            alertDialogBuilder?.create()?.show()
        } else {
            promptForReadStoragePermission()
        }
    }


    private fun promptForReadStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Checking for backup files", Toast.LENGTH_SHORT).show()
                checkBackUpFiles()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                checkBackUpFiles()
                restore_backup_button.isEnabled = true
                restore_backup_button.setTextColor(Color.WHITE)
                downlading_spinner.visibility = View.GONE
                restore_backup_button.setBackgroundResource(R.drawable.backup_button_active)
            }
        }
    }

    private fun checkBackUpFiles() {
        val backupDir = File("/storage/emulated/0/.CoolMeBackup")
        val backupDirExists = backupDir.exists()

        if (backupDirExists) {
            kotlin.runCatching {
                backupDir.listFiles()?.forEach { it.copyTo(filesDir, true) }
            }.onSuccess {
                Toast.makeText(applicationContext, "Backup restored successfully", Toast.LENGTH_SHORT).show()
                updateRestoredBackupStateInSharedPref()
                val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
                val intent = Intent(this, MemesDetailActivity::class.java)
                startActivity(intent, bundle)
            }
        } else {
            Toast.makeText(applicationContext, "No backup files found", Toast.LENGTH_SHORT).show()
            restore_backup_button.isEnabled = true
            restore_backup_button.setTextColor(Color.WHITE)
            downlading_spinner.visibility = View.GONE
            restore_backup_button.setBackgroundResource(R.drawable.backup_button_active)
        }
    }


    private fun setUpDownloadButton() {
        download_button.setOnClickListener {
            runOnUiThread {
                download_button.isEnabled = false
                downlading_spinner.visibility = View.VISIBLE
                download_button.setBackgroundResource(R.drawable.button_deactivated)

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
        startActivity(intent, bundle)
        finish()
    }

    private fun updateRestoredBackupStateInSharedPref(){
        val sharedPreferences = getSharedPreferences(resources.getString(R.string.user_shared_prefrences), Context.MODE_PRIVATE)
        val sharedPrefrencesEditor = sharedPreferences.edit()
        val key = resources.getString(R.string.has_restored_backup_files)
        sharedPrefrencesEditor.putBoolean(key, true).apply()
    }

    override fun updateDownloadObserver(currentSavedObjects: Int) {
        objectsSavedCounter = currentSavedObjects
        if (objectsSavedCounter == FirebaseMemeImagesDownloader.filesFromFirebaseCount)
            downloadCompleted = true
    }

    companion object {
        private const val READ_PERMISSION_CODE = 12
    }

}
