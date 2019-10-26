package com.example.coolme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*

class MemeImage private constructor(val name: String, val category: String, val imageId: Int) : Serializable {

    var imagePath = ""

    companion object {
        lateinit var filesDir: File
        lateinit var templateFolderPath: String
        lateinit var objectsDirectory: File
        lateinit var objectsPaperDirectory: String

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
            val memeImageFileName = "${MemeImage.objectsDirectory.path}/${memeImage.imageId}"
            val fileOutputStream = FileOutputStream(File(memeImageFileName))
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(memeImage)
            fileOutputStream.close()
            objectOutputStream.close()

            saveMemeImage(context, memeImage.imageId)
        }

        private fun saveMemeImage(context: Context, imageId: Int) {
            val file = File(templateFolderPath, "$imageId.PNG")
            val bitmapImage = BitmapFactory.decodeResource(context.resources, imageId)
            val outStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        }

        fun loadMemeObject(context: Context): List<MemeImage> {
            val listOfMemeImage = mutableListOf<MemeImage>()
            var fileInputStream: FileInputStream? = null
            var objectInputStream: ObjectInputStream? = null
            File(objectsDirectory.path).listFiles()?.forEach {
                fileInputStream = FileInputStream(File(it.toURI()))
                objectInputStream = ObjectInputStream(fileInputStream)
                val memeImage = objectInputStream?.readObject() as MemeImage
                memeImage.imagePath = "$templateFolderPath/${memeImage.imageId}.PNG"
                listOfMemeImage.add(memeImage)
            }
            fileInputStream?.close()
            objectInputStream?.close()
            return listOfMemeImage
        }


        fun createMemeImage(context: Context, name: String, category: String, imageId: Int) {
            val memeImage = MemeImage(name = name, category = category, imageId = imageId)
            saveMemeObject(context, memeImage)
        }
    }


}