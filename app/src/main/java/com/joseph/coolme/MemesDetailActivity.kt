package com.joseph.coolme

import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.TooltipCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.joseph.coolme.model.MemeImage
import com.joseph.coolme.view.CardStackAdapter
import com.joseph.coolme.view.MemeImageDiffCallback
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_memes_detail.*
import kotlinx.android.synthetic.main.download_meme_layout.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class MemesDetailActivity : AppCompatActivity(), CardStackListener {
    private val cardStackView by lazy { card_stack_view as CardStackView }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(MemeImage.loadSavedMemeImages()) }
    private lateinit var currentMemeImage: MemeImage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memes_detail)
        initializeViews()
        setupCardStackView()
        setupButtons()

    }


    private fun checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "Creating backup files", Toast.LENGTH_SHORT).show()
            createBackUpFiles()
        } else
            requestWriteStoragePermission()
    }

    private fun requestWriteStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val alertDialogBuilder: AlertDialog.Builder? = this.let {
                AlertDialog.Builder(it)
                        .setTitle("Permission is needed")
                        .setMessage("The application needs to access internal storage to back up your files")
                        .apply {
                            setPositiveButton("Okay", DialogInterface.OnClickListener { _, _ -> promptForWriteToStoragePermission() })
                            setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, _ ->
                                dialog.dismiss()
                            })
                        }
            }
            alertDialogBuilder?.create()?.show()
        } else {
            promptForWriteToStoragePermission()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_TO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Creating backup files", Toast.LENGTH_SHORT).show()
                createBackUpFiles()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun createBackUpFiles() {
        val backupSharedPrefsDir = File("/storage/emulated/0/.CoolMeBackup/shared_prefs")
        val sharedPrefs = File("${filesDir.parentFile?.path}/shared_prefs/${resources.getString(R.string.meme_images_shared_prefrences_key)}.xml")
        val backupFilesDir = File("/storage/emulated/0/.CoolMeBackup/files")
        val internalFiles = File(filesDir.path)

        if (!backupSharedPrefsDir.exists()) backupSharedPrefsDir.mkdir()
        val backupSharedPrefsFile = File("${backupSharedPrefsDir.path}/${resources.getString(R.string.meme_images_shared_prefrences_key)}.xml")

        if (!backupFilesDir.exists()) backupFilesDir.mkdir()

        kotlin.runCatching {
            sharedPrefs.copyTo(backupSharedPrefsFile, true)
            internalFiles.listFiles()?.forEach { it.copyRecursively(File(backupFilesDir.absolutePath.plus("/${it.name}")),true) }
        }.onSuccess { Toast.makeText(applicationContext, "Backup created successfully", Toast.LENGTH_SHORT).show() }
                .onFailure { exception ->
                    println("EEXXEEPP: ${exception.message}")
                    Toast.makeText(applicationContext, "Could not create backup", Toast.LENGTH_SHORT).show()
                }
    }

    private fun promptForWriteToStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_TO_PERMISSION_CODE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }

    private fun initializeViews() {
        TooltipCompat.setTooltipText(backup_button, "Backup files internally")
        TooltipCompat.setTooltipText(add_button, "Add meme image")
        TooltipCompat.setTooltipText(delete_button, "Delete meme image")
        TooltipCompat.setTooltipText(rewind_button, "Rewind last image")
        TooltipCompat.setTooltipText(share_button, "Share this image")
        TooltipCompat.setTooltipText(edit_button, "Edit this image")
        layViewsUnderStatusBar()
        setStatusTextColorToDark()
    }


    private fun setupButtons() {
        onClickSaveBackup()
        onClickAddImageMemes()
        onClickDeleteMemeFromStorage()
        onClickRewind()
        onClickShareCurrentImage()
    }

    private fun onClickShareCurrentImage() {
        share_button.setOnClickListener {
            val fileToShare = File(currentMemeImage.imageUri)
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, fileToShare)?.let {
                val shareIntent = Intent()
                        .setAction(Intent.ACTION_SEND)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .setDataAndType(it, contentResolver.getType(it))
                        .putExtra(Intent.EXTRA_STREAM, it)
                startActivity(Intent.createChooser(shareIntent, "Share meme to:"))
            }
        }
    }

    private fun onClickSaveBackup() {
        backup_button.setOnClickListener {
            checkReadExternalStoragePermission()
        }
    }

    fun onClickAddImageMemes() {
        add_button.setOnClickListener {
            Toast.makeText(applicationContext, "Meme Was added", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickDeleteMemeFromStorage() {
        delete_button.setOnClickListener {
            if (File(MemeImage.memeTemplatesDirectory).listFiles()?.size ?: 0 > 6) {
                removeFirst(1)
                Toast.makeText(applicationContext, "Meme was deleted", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(applicationContext, "Cannot delete all memes", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickRewind() {
        rewind_button.setOnClickListener {
            Log.d("POSSSS", "Manager pos: ${manager.topPosition}, Manager count: ${manager.itemCount}, adapter count: ${adapter.itemCount}")
            if (manager.topPosition > 0) {
                if (manager.topPosition == adapter.itemCount) {
                    paginate()
                } else {
                    val setting = RewindAnimationSetting.Builder()
                            .setDirection(Direction.Bottom)
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(DecelerateInterpolator())
                            .build()
                    manager.setRewindAnimationSetting(setting)
                    cardStackView.rewind()

                }
            }
        }
    }


    private fun layViewsUnderStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun setStatusTextColorToDark() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    //------------------ copied code -------------------//

//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawers()
//        } else {
//            super.onBackPressed()
//        }
//    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
//        currentMemeImage = MemeImage.loadSavedMemeImages()[manager.topPosition]

    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")

    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackViewDD", "onCardAppeared: ($position) ${textView.text}")
        Log.d("CardStackViewDD", "DCurrent swipe count = ${manager.topPosition}")
        currentMemeImage = MemeImage.loadSavedMemeImages().find { it.name == textView.text }!!
        Log.d("CardStackViewDD", "DCurrent MemeName = ${currentMemeImage.name}")

    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun setupCardStackView() {
        initialize()
    }


    private fun initialize() {
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.1f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = true
            }
        }
        CardStackAdapter.setContextObserver(this)
    }


    private fun paginate() {
        val old = adapter.getMemeImages()
        val new = old.plus(MemeImage.loadSavedMemeImages())
        val callback = MemeImageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setMemeImages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeFirst(size: Int) {
        if (adapter.getMemeImages().isEmpty()) {
            return
        }
        val old = adapter.getMemeImages()
        val new = mutableListOf<MemeImage>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)

            }
            MemeImage.deleteMemeImage(currentMemeImage)
        }

        val callback = MemeImageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setMemeImages(new)
        result.dispatchUpdatesTo(adapter)
    }


    private fun reload() {
        val old = adapter.getMemeImages()
        val new = MemeImage.loadSavedMemeImages()
        val callback = MemeImageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setMemeImages(new)
        result.dispatchUpdatesTo(adapter)
    }

    companion object {
        private const val WRITE_TO_PERMISSION_CODE = 202
    }

}
