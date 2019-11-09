package com.joseph.coolme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.joseph.coolme.model.MemeImage
import kotlinx.android.synthetic.main.add_bottom_sheet_layout.*
import java.io.File

class AddMemeBottomSheet(val activity: AppCompatActivity) : BottomSheetDialogFragment() {

    private lateinit var memeImageName: String
    private lateinit var memeImageCategory: String
    private lateinit var memeImageFile: File


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.add_bottom_sheet_layout, container, false)
    }

    override fun onResume() {
        super.onResume()
        setImageViewListener()
        save_button.setOnClickListener {
            saveMemeImageToInternalStorage()
            Toast.makeText(this.context, "Meme Image was saved", Toast.LENGTH_SHORT).show()
            (activity as BottomSheetObserver).updateFromBottomSheet()
            this.dismiss()
        }
    }


    private fun saveMemeImageToInternalStorage() {
        memeImageName = add_meme_name_edit_text.text.toString()
        memeImageCategory = add_meme_category_edit_text.text.toString()
        MemeImage.createMemeImage(memeImageName, memeImageCategory, memeImageFile.path.toString())
    }


    private fun setImageViewListener() {
        add_meme_image_view.setOnClickListener {
            ImagePicker.with(this)
                    .galleryOnly() // User can only select image from Gallery(Optional)
                    .start(IMAGE_REQ_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageFile = ImagePicker.getFile(data)
            if (imageFile != null) {
                Log.d("ADDING MEME TO STORAGE", "captured file at path: ${imageFile.path}")
                Log.d("ADDING MEME TO STORAGE", "captured file at URI: ${imageFile.toURI()}")

                memeImageFile = imageFile
                add_meme_image_view.scaleType = ImageView.ScaleType.CENTER_CROP
                Glide.with(add_meme_image_view).load(imageFile).into(add_meme_image_view)
                add_meme_name_edit_text.isEnabled = true
                add_meme_category_edit_text.isEnabled = true
                verifyCorrectAddImageFields()
            }
        }
    }


    private fun verifyCorrectAddImageFields() {

        val textWatcher = object : TextWatcher {
            var memeName: CharSequence? = ""
            var memeCategory: CharSequence? = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                memeName = add_meme_name_edit_text.text?.trim()
                memeCategory = add_meme_category_edit_text.text?.trim()
                save_button.isEnabled = !memeName.isNullOrBlank() && !memeCategory.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
                if (memeName?.isBlank() == true) add_meme_name_edit_text.error =
                        "field cannot be empty"
                if (memeCategory?.isBlank() == true) add_meme_category_edit_text.error =
                        "field cannot be empty"

            }

        }

        add_meme_name_edit_text.addTextChangedListener(textWatcher)
        add_meme_category_edit_text.addTextChangedListener(textWatcher)

    }

    companion object {
        const val IMAGE_REQ_CODE = 102
    }


}
