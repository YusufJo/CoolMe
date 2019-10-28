package com.joseph.coolme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.*
import java.security.SecureRandom
import kotlin.math.absoluteValue

class MemeImage private constructor(val name: String, val category: String, val imageUri: String) : Serializable {

    var imagePath = ""


    companion object {
        const val serialVersionUID = -679693852303716616L
        val firebaseMemesDir = FirebaseStorage.getInstance().getReferenceFromUrl("gs://coolme-yaqout.appspot.com/memes/meme_templates/")
        lateinit var filesDir: File
        lateinit var templateFolderPath: String
        lateinit var objectsDirectory: File
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
        }

        fun writeImageDirectoryOfMemes() {
            val directoryName = "Templates"
            val templateFolder = File("${filesDir.path}/$directoryName")
            if (!filesDir.listFiles()?.map { it.name }?.contains(directoryName)!!)
                templateFolder.mkdir()
            templateFolderPath = templateFolder.path
        }

        private fun saveMemeObject(context: Context, memeImage: MemeImage, saveToTemplate: Boolean, imageUri: String = "", imageName: String) {
            val secureRandomId = if (saveToTemplate) SecureRandom().nextLong().absoluteValue.toString() else imageName
            val memeObjectFileName = "${MemeImage.objectsDirectory.path}/$secureRandomId"
            fileOutputStream = FileOutputStream(File(memeObjectFileName))
            objectOutputStream = ObjectOutputStream(fileOutputStream)
            memeImage.imagePath = if (saveToTemplate) "$templateFolderPath/${secureRandomId}.PNG" else imageUri.removePrefix("file://")
            objectOutputStream.writeObject(memeImage)

            fileOutputStream.close()
            objectOutputStream.close()

            if (saveToTemplate) saveMemeImage(context, memeImage.imageUri, secureRandomId)
        }

        fun saveMemeImage(context: Context, imageUri: String, secureRandomId: String) {
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


        fun createMemeImage(context: Context, name: String, category: String, imageUri: String, imageName: String, saveToTemplate: Boolean = true): MemeImage {
            val memeImage = MemeImage(name = name, category = category, imageUri = imageUri)
            saveMemeObject(context, memeImage, saveToTemplate, imageUri, imageName)
            return memeImage
        }


        fun downloadDefaultMemesFromFirebase() {
            firebaseMemesDir.listAll().addOnSuccessListener { listResult ->
                listResult.items.forEach {
                    val imageFile = File("$templateFolderPath/${it.name}")
                    imageFile.createNewFile()
                    it.getFile(imageFile)
                    println("File saved at: ${imageFile.path}, file uri: ${imageFile.toURI()}")
                }
            }
        }
    }


}