package com.example.coolme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import java.io.*
import java.net.URL
import java.security.SecureRandom
import kotlin.math.absoluteValue

class MemeImage private constructor(val name: String, val category: String, val imageUri: String) : Serializable {

    var imagePath = ""

    companion object {
        const val serialVersionUID = -679693852303716616L
        lateinit var filesDir: File
        lateinit var templateFolderPath: String
        lateinit var objectsDirectory: File
        lateinit var objectsPaperDirectory: String
        lateinit var fileOutputStream: FileOutputStream
        lateinit var objectOutputStream: ObjectOutputStream
        lateinit var fileInputStream: FileInputStream
        lateinit var objectInputStream: ObjectInputStream


        fun writeObjectDirectoryOfMemes() {
            val objectsDirectoryName = "Objects"
            val objectsDirectory = File("${filesDir.path}/$objectsDirectoryName")
            if (!filesDir.listFiles()?.map { it.name }?.contains(objectsDirectoryName)!!)
                objectsDirectory.mkdir()
            this.objectsDirectory = objectsDirectory
            objectsPaperDirectory = "${objectsDirectory.path}/MemeImageObjects"
        }

        fun writeImageDirectoryOfMemes() {
            val directoryName = "Templates"
            val templateFolder = File("${filesDir.path}/$directoryName")
            if (!filesDir.listFiles()?.map { it.name }?.contains(directoryName)!!)
                templateFolder.mkdir()
            templateFolderPath = templateFolder.path
        }

        private fun saveMemeObject(context: Context, memeImage: MemeImage) {
            val secureRandomId = SecureRandom().nextInt().absoluteValue.toString()
            val memeImageFileName = "${MemeImage.objectsDirectory.path}/$secureRandomId"
            fileOutputStream = FileOutputStream(File(memeImageFileName))
            objectOutputStream = ObjectOutputStream(fileOutputStream)
            memeImage.imagePath = "$templateFolderPath/${secureRandomId}.PNG"
            objectOutputStream.writeObject(memeImage)

            fileOutputStream.close()
            objectOutputStream.close()

            saveMemeImage(context, memeImage.imageUri, secureRandomId)
        }

        private fun saveMemeImage(context: Context, imageUri: String, secureRandomId: String) {
            val file = File(templateFolderPath, "$secureRandomId.PNG")
            val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
            val bitmap = BitmapFactory.decodeStream(inputStream)
//            val bitmapImage = BitmapFactory.decodeResource(context.resources, imageId)
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            inputStream?.close()
            fileOutputStream.close()

        }

        fun loadMemeObject(): List<MemeImage> {
            val listOfMemeImage = mutableListOf<MemeImage>()
            File(objectsDirectory.path).listFiles()?.forEach {
                fileInputStream = FileInputStream(File(it.toURI()))
                objectInputStream = ObjectInputStream(fileInputStream)
                val memeImage = objectInputStream.readObject() as MemeImage
                listOfMemeImage.add(memeImage)
                fileInputStream.close()
                objectInputStream.close()

            }
            return listOfMemeImage
        }


        fun createMemeImage(context: Context, name: String, category: String, imageUri: String): MemeImage {
            val memeImage = MemeImage(name = name, category = category, imageUri = imageUri)
            saveMemeObject(context, memeImage)
            return memeImage
        }
    }


}