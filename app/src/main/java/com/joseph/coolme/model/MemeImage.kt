package com.joseph.coolme.model

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.*
import java.net.URI
import java.security.SecureRandom
import kotlin.math.absoluteValue
import android.graphics.Paint.FILTER_BITMAP_FLAG
import com.joseph.coolme.R

/** This class is responsible for creating, saving, receiving and downloading image memes from Firebase storage.
 *
 *  @constructor
 *  @param name                     : describes a name for the meme image which can be searched by in a list of memeImages.
 *  @param category                 : describes a category for the meme image which can be searched by in a list of memeImages.
 *  @param imageUri                 : the uri of an image which can refer to a png saved in the memes template directory or can be a reference
 *                                      to an image received by an implicit intent from other app.
 */
class MemeImage private constructor(val name: String, val category: String, val imageUri: String) {
//    var imageBitmapByteArray: ByteArray = byteArrayOf()

    /** Static core of MemeImage::class
     *  @property memeTemplatesDirectory : path to directory of images which holds downloaded meme images from firebase
     */
    companion object : ContextObservable {

        lateinit var memeTemplatesDirectory: String
        private lateinit var activity: AppCompatActivity


        override fun setContextObserver(activity: AppCompatActivity) {
            Companion.activity = activity
            val memeTemplateDir = File(activity.filesDir, "Templates")
            memeTemplateDir.mkdir()
            memeTemplatesDirectory = memeTemplateDir.path
        }

        private fun getMemesSharedPreferences(): SharedPreferences {
            val context = activity.applicationContext
            val key = context.resources.getString(R.string.meme_images_shared_prefrences_key)
            return context.getSharedPreferences(key, Context.MODE_PRIVATE)
        }


        private fun saveSingleMemeInSharedPrefrences(memeImage: MemeImage) {
            val secureRandomId = SecureRandom().nextLong().absoluteValue.toString()
            val sharedPreferences = getMemesSharedPreferences()
            val preferenceEditor = sharedPreferences.edit()
            val myJsonObject = Gson().toJson(memeImage)
            preferenceEditor.putString(secureRandomId, myJsonObject).apply()
            saveMemeImageToMemeTemplatesDir(secureRandomId, memeImage.imageUri)
        }

        private fun saveMemeImageToMemeTemplatesDir(secureRandom: String, imageUri: String) {
            val imageFile = File(memeTemplatesDirectory, "$secureRandom.PNG")
            File(URI(imageUri)).copyTo(imageFile, true)
        }


        fun loadSavedMemeImages(): List<MemeImage> {
            val sharedPreferences = getMemesSharedPreferences()
            return sharedPreferences.all.keys.map { sharedPreferences.getString(it, "") }
                    .filterNot { it.isNullOrBlank() }.map { Gson().fromJson(it, MemeImage::class.java) }
        }


        fun createMemeImage(name: String, category: String, imageUri: String, saveMultibleObjects: Boolean = false): MemeImage {
            val memeImage = MemeImage(name = name, category = category, imageUri = imageUri)
            if (!saveMultibleObjects) saveSingleMemeInSharedPrefrences(memeImage)
            return memeImage
        }

        fun deleteMemeImage(memeImage: MemeImage) {
            File(memeImage.imageUri).delete()
            val memeImageId = memeImage.imageUri.replace("$memeTemplatesDirectory/", "")
            getMemesSharedPreferences().edit().remove(memeImageId).apply()
        }

        fun deleteMemeImage(position: Int) {
            val memeImageToDelete = loadSavedMemeImages().get(position)
            deleteMemeImage(memeImageToDelete)
        }


    }

    val getBitmapImage: Bitmap get() = BitmapFactory.decodeFile(this.imageUri)

}