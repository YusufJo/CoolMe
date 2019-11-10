package com.joseph.coolme

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.joseph.coolme.model.MemeImage
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_add_meme.*
import kotlinx.android.synthetic.main.add_bottom_sheet_layout.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddMemeActivity : AppCompatActivity() {
    private lateinit var memeImageName: String
    private lateinit var memeImageCategory: String
    private lateinit var tempFile: File
    private lateinit var uriData: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meme)

        MemeImage.setContextObserver(this)
        val uriData1 = intent.clipData?.getItemAt(0)?.uri
        if (uriData1 != null) {
            uriData = uriData1
        }

    }

    override fun onStart() {
        super.onStart()

        val inputStream = contentResolver.openInputStream(uriData)
        tempFile = File(cacheDir, "receivedImageFile.PNG")
        tempFile.createNewFile()

        copyStreamToFile(inputStream!!, tempFile)

        Glide.with(blurry_image_add_meme_activity)
                .load(uriData)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(blurry_image_add_meme_activity)

        Glide.with(image_add_meme_activity).load(uriData).into(image_add_meme_activity)

        add_meme_name_edit_text_activity.isEnabled = true
        add_meme_category_edit_text_activity.isEnabled = true
    }

    override fun onResume() {
        super.onResume()

        verifyCorrectAddImageFields()
        save_button_add_meme_activity.setOnClickListener {
            saveMemeImageToInternalStorage()
            Toast.makeText(applicationContext, "Meme Image was saved", Toast.LENGTH_SHORT).show()
            clearCache()
            finish()
        }
    }

    private fun clearCache(){
        cacheDir.listFiles()?.forEach { it.deleteRecursively() }
        codeCacheDir.listFiles()?.forEach { it.deleteRecursively() }
        clearFindViewByIdCache()
    }

    private fun saveMemeImageToInternalStorage() {
        memeImageName = add_meme_name_edit_text_activity.text.toString()
        memeImageCategory = add_meme_category_edit_text_activity.text.toString()
        MemeImage.createMemeImage(memeImageName, memeImageCategory, tempFile.path.toString())
    }


    private fun verifyCorrectAddImageFields() {

        val textWatcher = object : TextWatcher {
            var memeName: CharSequence? = ""
            var memeCategory: CharSequence? = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                memeName = add_meme_name_edit_text_activity.text?.trim()
                memeCategory = add_meme_category_edit_text_activity.text?.trim()
                save_button_add_meme_activity.isEnabled = !memeName.isNullOrBlank() && !memeCategory.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
                if (memeName?.isBlank() == true) add_meme_name_edit_text_activity.error =
                        "field cannot be empty"
                if (memeCategory?.isBlank() == true) add_meme_category_edit_text_activity.error =
                        "field cannot be empty"
            }
        }

        add_meme_name_edit_text_activity.addTextChangedListener(textWatcher)
        add_meme_category_edit_text_activity.addTextChangedListener(textWatcher)

    }


    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }


}
