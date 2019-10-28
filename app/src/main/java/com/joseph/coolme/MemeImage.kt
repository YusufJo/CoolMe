package com.joseph.coolme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.*
import java.security.SecureRandom
import kotlin.math.absoluteValue

class MemeImage private constructor(val name: String, val category: String, val imageUri: String) : Serializable {

    lateinit var imageBitmapByteArray: ByteArray

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

//        private fun saveMemeObject(context: Context, memeImage: MemeImage, saveToTemplate: Boolean, imageUri: String = "", imageName: String) {
//            val secureRandomId = if (saveToTemplate) SecureRandom().nextLong().absoluteValue.toString() else imageName
//            val memeObjectFileName = "${MemeImage.objectsDirectory.path}/$secureRandomId"
//            fileOutputStream = FileOutputStream(File(memeObjectFileName))
//            objectOutputStream = ObjectOutputStream(fileOutputStream)
//            memeImage.imagePath = if (saveToTemplate) "$templateFolderPath/${secureRandomId}.PNG" else imageUri.removePrefix("file://")
//            objectOutputStream.writeObject(memeImage)
//
//            fileOutputStream.close()
//            objectOutputStream.close()
//
//            if (saveToTemplate) saveMemeImage(context, memeImage.imageUri, secureRandomId)
//        }

        private fun saveMemeObject(memeImage: MemeImage, imageUri: String) {
            val secureRandomId = SecureRandom().nextLong().absoluteValue.toString()
            val memeObjectFilePath = "${objectsDirectory.path}/$secureRandomId"
            fileOutputStream = FileOutputStream(File(memeObjectFilePath))
            objectOutputStream = ObjectOutputStream(fileOutputStream)
            // save image byte array here
            memeImage.imageBitmapByteArray = bitmapToByteArrayConvert(imageUri)
            objectOutputStream.writeObject(memeImage)
            fileOutputStream.close()
            objectOutputStream.close()
        }

        private fun bitmapToByteArrayConvert(imageUri: String): ByteArray {
            val bitmap = BitmapFactory.decodeFile(imageUri.removePrefix("file://"))
            val byteOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream)
            return byteOutputStream.toByteArray()
        }

//        fun saveMemeImage(context: Context, imageUri: String, secureRandomId: String) {
//            val file = File(templateFolderPath, "$secureRandomId.PNG")
//            val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
//            val bitmap = BitmapFactory.decodeStream(inputStream)
////            val bitmapImage = BitmapFactory.decodeResource(context.resources, imageId)
//            fileOutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
//            inputStream?.close()
//            fileOutputStream.close()
//
//        }

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


        fun createMemeImage(name: String, category: String, imageUri: String): MemeImage {
            val memeImage = MemeImage(name = name, category = category, imageUri = imageUri)
            saveMemeObject(memeImage, imageUri)
            return memeImage
        }


        fun downloadDefaultMemesFromFirebase() {
            firebaseMemesDir.listAll().addOnSuccessListener { listResult ->

                val itemCount = listResult.items.size
                var currentCount = 0
                listResult.items.forEach {
                    println("LOGTAG: Item got is: ${it.name}")
                    val imageFile = File("$templateFolderPath/${it.name}")
                    imageFile.createNewFile()
                    it.getFile(imageFile).addOnSuccessListener {
                        currentCount += 1
                        println("LOGTAG: Success for ${imageFile.path}")
                        println("LOGTAG: count = $currentCount")
                        if (currentCount == itemCount) {
                            println("LOGTAG: Finished download")
                            saveDefaultMemes()
                        }
                    }
                }
            }
        }

        private fun saveDefaultMemes() {
            createMemeImage("بردو عاوز ايه", "احمد عبدالعزيز", "$templateFolderPath/7920923873988338478.PNG")
            createMemeImage("يا بتاع نسوان", "ابو العربي", "$templateFolderPath/4133849861841304319.PNG")
            createMemeImage("حاولت اعمل حاجه صح", "الباشا تلميذ", "$templateFolderPath/7543383608074692613.PNG")
            createMemeImage("Blank blue button", "miscellaneous", "$templateFolderPath/9123157803230600046.PNG")
            createMemeImage("ما تقول يا عم الحج", "بوحه", "$templateFolderPath/2651468130074524971.PNG")
            createMemeImage("و انا بدبح الخاروف ٢", "بوشكاش", "$templateFolderPath/5348664233530608244.PNG")
            createMemeImage("و انا بدبح الخاروف ١", "بوشكاش", "$templateFolderPath/8775123745978246208.PNG")
            createMemeImage("Cat taking selfie", "miscellaneous", "$templateFolderPath/988721319133559770.PNG")
            createMemeImage("Computer guy", "miscellaneous", "$templateFolderPath/7655274127381208375.PNG")
            createMemeImage("Crying cat 1", "miscellaneous", "$templateFolderPath/2899901639391200118.PNG")
            createMemeImage("Distracted boyfriend", "miscellaneous", "$templateFolderPath/5313170327333746694.PNG")
            createMemeImage("Don't you Squidward", "miscellaneous", "$templateFolderPath/6586496531261769900.PNG")
            createMemeImage("Hotline bling", "miscellaneous", "$templateFolderPath/6476295671598214982.PNG")
            createMemeImage("كان نفسي اقولك", "الباشا تلميذ", "$templateFolderPath/3355234225575772824.PNG")
            createMemeImage("مش ده اللي متجوز البنت الخبره", "التجربه الدنماركيه", "$templateFolderPath/7336141185885406206.PNG")
            createMemeImage("Finding neverland", "miscellaneous", "$templateFolderPath/3583368914438023384.PNG")
            createMemeImage("Gaven Shook 2", "miscellaneous", "$templateFolderPath/5489744819494715310.PNG")
            createMemeImage("Gaven Shook 1", "miscellaneous", "$templateFolderPath/8444310702317302581.PNG")
            createMemeImage("Hamdi el-Wazeer cat", "miscellaneous", "$templateFolderPath/908598265378916996.PNG")
            createMemeImage("I Know That feeling Bro", "miscellaneous", "$templateFolderPath/6456524840913374705.PNG")
            createMemeImage("Shaking face", "miscellaneous", "$templateFolderPath/7298379275850517893.PNG")
            createMemeImage("Imagination Spongebob", "miscellaneous", "$templateFolderPath/4630412994754889961.PNG")
            createMemeImage("Is This a pigeon", "miscellaneous", "$templateFolderPath/4562135145322374822.PNG")
            createMemeImage("البنت الغلسه", "الليمبي", "$templateFolderPath/1538572912883439525.PNG")
            createMemeImage("Leonardo Dicaprio cheers", "miscellaneous", "$templateFolderPath/6230723693849050263.PNG")
            createMemeImage("Little sad kid 1", "miscellaneous", "$templateFolderPath/8670077008252448471.PNG")
            createMemeImage("Little sad kid 2", "miscellaneous", "$templateFolderPath/591572363333449481.PNG")
            createMemeImage("Little sad kid 3", "miscellaneous", "$templateFolderPath/7529040319098022325.PNG")
            createMemeImage("Little sad kid 4", "miscellaneous", "$templateFolderPath/4842827181736038135.PNG")
            createMemeImage("LOL rage face", "miscellaneous", "$templateFolderPath/8856023176284614346.PNG")
            createMemeImage("كنت يهودي عبقري", "محي اسماعيل", "$templateFolderPath/834557716296681972.PNG")
            createMemeImage("Okay rage face", "miscellaneous", "$templateFolderPath/1784982350859253231.PNG")
            createMemeImage("Question rage face", "miscellaneous", "$templateFolderPath/6341702270607501759.PNG")
            createMemeImage("Running away balloon", "miscellaneous", "$templateFolderPath/8251560209023487976.PNG")
            createMemeImage("Scared cat", "miscellaneous", "$templateFolderPath/2477530499148820844.PNG")
            createMemeImage("Silly cat", "miscellaneous", "$templateFolderPath/6315529842295112018.PNG")
            createMemeImage("Awesome Awkward Penguin", "miscellaneous", "$templateFolderPath/8849463044200823824.PNG")
            createMemeImage("Silly Peter Parker", "miscellaneous", "$templateFolderPath/5027783689869495966.PNG")
            createMemeImage("Two buttons", "miscellaneous", "$templateFolderPath/8864323811965714575.PNG")
            createMemeImage("Weird cat", "miscellaneous", "$templateFolderPath/5294591373114860185.PNG")
            createMemeImage("Woman yelling at a cat", "miscellaneous", "$templateFolderPath/3118701756182461752.PNG")
            createMemeImage("x x everywhere", "miscellaneous", "$templateFolderPath/2789549287549790465.PNG")
            createMemeImage("Young Cardi-B", "احمد عبدالعزيز", "$templateFolderPath/4051905074816386882.PNG")
            createMemeImage("حمدي الوزير", "قبضه الهلالي", "$templateFolderPath/7479651419850771894.PNG")
        }
    }

    val getBitmapImage: Bitmap get() = BitmapFactory.decodeByteArray(imageBitmapByteArray, 0, imageBitmapByteArray.size)


}