package com.joseph.coolme.photoEditor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joseph.coolme.BuildConfig
import com.joseph.coolme.R
import com.joseph.coolme.model.MemeImage
import ja.burhanrashid52.photoeditor.*
import kotlinx.android.synthetic.main.activity_edit_and_share_meme.*
import java.io.File

class EditAndShareMeme : BaseActivity(), OnPhotoEditorListener,
        View.OnClickListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected {


    private val logTag = EditAndShareMeme::class.java.simpleName
    private var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mTxtCurrentTool: TextView? = null
    private var mRvTools: RecyclerView? = null
    private val mEditingToolsAdapter = EditingToolsAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_and_share_meme)

        val file = File(intent.getStringExtra("ImageToEdit")!!)

        photoEditorView.source.setImageBitmap(BitmapFactory.decodeFile(file.path))



        initViews()

        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)

        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools!!.layoutManager = llmTools
        mRvTools!!.adapter = mEditingToolsAdapter


        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView!!)
                .setPinchTextScalable(true)
                .build()

        mPhotoEditor!!.setOnPhotoEditorListener(this)

    }

    private fun initViews() {
        val imgUndo: ImageView = findViewById(R.id.imgUndo)
        val imgRedo: ImageView = findViewById(R.id.imgRedo)
        val imgShare: ImageView = findViewById(R.id.imgShare)
        val imgClose: ImageView = findViewById(R.id.imgClose)

        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool)
        mRvTools = findViewById(R.id.rvConstraintTools)

        imgUndo.setOnClickListener(this)

        imgRedo.setOnClickListener(this)

        imgShare.setOnClickListener(this)

        imgClose.setOnClickListener(this)

    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode1 ->
            val styleBuilder = TextStyleBuilder()
            styleBuilder.withTextColor(colorCode1)

            mPhotoEditor!!.editText(rootView, inputText, styleBuilder)
            mTxtCurrentTool!!.setText(R.string.label_text)
        }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(logTag, "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]")
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(logTag, "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]")
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(logTag, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(logTag, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onClick(view: View) {
        when (view.id) {

            R.id.imgUndo -> mPhotoEditor!!.undo()

            R.id.imgRedo -> mPhotoEditor!!.redo()

            R.id.imgShare -> shareImage()

            R.id.imgClose -> onBackPressed()
        }
    }

    @SuppressLint("MissingPermission")
    private fun shareImage() {

        val file = File(cacheDir.path, "EditedImage.png")
        val saveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(true)
                .build()



        mPhotoEditor!!.saveAsFile(file.absolutePath, saveSettings, object : PhotoEditor.OnSaveListener {
            override fun onSuccess(@NonNull imagePath: String) {
                // share image here
                mPhotoEditorView!!.source.setImageURI(Uri.fromFile(File(imagePath)))

                val fileToShare = File(MemeImage.memeTemplatesDirectory,"EditedImage.PNG")
                val currentFile = File(imagePath)
                currentFile.copyTo(fileToShare,true)

                FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID, fileToShare)?.let {
                    val shareIntent = Intent()
                            .setAction(Intent.ACTION_SEND)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .setDataAndType(it, contentResolver.getType(it))
                            .putExtra(Intent.EXTRA_STREAM, it)
                    startActivity(Intent.createChooser(shareIntent, "Share meme with"))
                }

            }

            override fun onFailure(@NonNull exception: Exception) {
                hideLoading()
                showSnackbar("Failed to Share Image")
            }
        })


    }

    override fun onStickerClick(bitmap: Bitmap) {
        mPhotoEditor!!.addImage(bitmap)
        mTxtCurrentTool!!.setText(R.string.label_sticker)
    }


    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you really want to discard edits?!")
        builder.setPositiveButton("Share") { _, _ -> shareImage() }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.setNeutralButton("Discard") { _, _ -> finish() }
        builder.create().show()

    }


    override fun onToolSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.TEXT -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)

                    mPhotoEditor!!.addText(inputText, styleBuilder)
                    mTxtCurrentTool!!.setText(R.string.label_text)
                }
            }

            ToolType.STICKER -> mStickerBSFragment!!.show(supportFragmentManager, mStickerBSFragment!!.tag)
        }
    }


    override fun onBackPressed() {
        if (!mPhotoEditor!!.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }


}
