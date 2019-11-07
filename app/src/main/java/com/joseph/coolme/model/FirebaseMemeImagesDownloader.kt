package com.joseph.coolme.model

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.joseph.coolme.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File


class FirebaseMemeImagesDownloader {

    companion object : ContextObservable, DownloadObservable {
        val firebaseMemesDir = FirebaseStorage.getInstance().getReferenceFromUrl("gs://coolme2.appspot.com/memes/")
        private lateinit var activity: AppCompatActivity
        private lateinit var downloadObserver: DownloadObserver
        var filesFromFirebaseCount: Int = 0
        private var currentSavedImages = 0

        fun downloadDefaultMemesFromFirebase() {
            firebaseMemesDir.listAll().addOnSuccessListener { listResult ->

                filesFromFirebaseCount = listResult.items.size
                var currentCount = 0
                listResult.items.forEach {
                    println("LOGTAG: Item got is: ${it.name}")
                    val imageFile = File(MemeImage.memeTemplatesDirectory, it.name)
                    imageFile.createNewFile()
                    it.getFile(imageFile).addOnSuccessListener {
                        currentCount += 1
                        println("LOGTAG: Success for ${imageFile.path}")
                        println("LOGTAG: count = $currentCount")
                        if (currentCount == filesFromFirebaseCount) {
                            println("LOGTAG: Finished download")
                            saveDefaultMemes()
                        }
                    }
                }
            }
        }

        private fun saveDefaultMemes() {
            runBlocking {
                launch {
                    listOf(
                            MemeImage.createMemeImage("بردو عاوز ايه", "احمد عبدالعزيز", "${MemeImage.memeTemplatesDirectory}/7920923873988338478.PNG", true),
                            MemeImage.createMemeImage("يا بتاع نسوان", "ابو العربي", "${MemeImage.memeTemplatesDirectory}/4133849861841304319.PNG", true),
                            MemeImage.createMemeImage("حاولت اعمل حاجه صح", "الباشا تلميذ", "${MemeImage.memeTemplatesDirectory}/7543383608074692613.PNG", true),
                            MemeImage.createMemeImage("Blank blue button", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/9123157803230600046.PNG", true),
                            MemeImage.createMemeImage("ما تقول يا عم الحج", "بوحه", "${MemeImage.memeTemplatesDirectory}/2651468130074524971.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(

                            MemeImage.createMemeImage("و انا بدبح الخاروف ٢", "بوشكاش", "${MemeImage.memeTemplatesDirectory}/5348664233530608244.PNG", true),
                            MemeImage.createMemeImage("و انا بدبح الخاروف ١", "بوشكاش", "${MemeImage.memeTemplatesDirectory}/8775123745978246208.PNG", true),
                            MemeImage.createMemeImage("Cat taking selfie", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/988721319133559770.PNG", true),
                            MemeImage.createMemeImage("Computer guy", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/7655274127381208375.PNG", true),
                            MemeImage.createMemeImage("Crying cat 1", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/2899901639391200118.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(
                            MemeImage.createMemeImage("Distracted boyfriend", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/5313170327333746694.PNG", true),
                            MemeImage.createMemeImage("Don't you Squidward", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/6586496531261769900.PNG", true),
                            MemeImage.createMemeImage("Hotline bling", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/6476295671598214982.PNG", true),
                            MemeImage.createMemeImage("كان نفسي اقولك", "الباشا تلميذ", "${MemeImage.memeTemplatesDirectory}/3355234225575772824.PNG", true),
                            MemeImage.createMemeImage("مش ده اللي متجوز البنت الخبره", "التجربه الدنماركيه", "${MemeImage.memeTemplatesDirectory}/7336141185885406206.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(

                            MemeImage.createMemeImage("Finding neverland", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/3583368914438023384.PNG", true),
                            MemeImage.createMemeImage("Gaven Shook 2", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/5489744819494715310.PNG", true),
                            MemeImage.createMemeImage("Gaven Shook 1", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/8444310702317302581.PNG", true),
                            MemeImage.createMemeImage("Hamdi el-Wazeer cat", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/908598265378916996.PNG", true),
                            MemeImage.createMemeImage("I Know That feeling Bro", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/6456524840913374705.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(
                            MemeImage.createMemeImage("Shaking face", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/7298379275850517893.PNG", true),
                            MemeImage.createMemeImage("Imagination Spongebob", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/4630412994754889961.PNG", true),
                            MemeImage.createMemeImage("Is This a pigeon", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/4562135145322374822.PNG", true),
                            MemeImage.createMemeImage("البنت الغلسه", "الليمبي", "${MemeImage.memeTemplatesDirectory}/1538572912883439525.PNG", true),
                            MemeImage.createMemeImage("Leonardo Dicaprio cheers", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/6230723693849050263.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(
                            MemeImage.createMemeImage("Little sad kid 1", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/8670077008252448471.PNG", true),
                            MemeImage.createMemeImage("Little sad kid 2", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/591572363333449481.PNG", true),
                            MemeImage.createMemeImage("Little sad kid 3", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/7529040319098022325.PNG", true),
                            MemeImage.createMemeImage("Little sad kid 4", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/4842827181736038135.PNG", true),
                            MemeImage.createMemeImage("LOL rage face", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/8856023176284614346.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(
                            MemeImage.createMemeImage("كنت يهودي عبقري", "محي اسماعيل", "${MemeImage.memeTemplatesDirectory}/834557716296681972.PNG", true),
                            MemeImage.createMemeImage("Okay rage face", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/1784982350859253231.PNG", true),
                            MemeImage.createMemeImage("Question rage face", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/6341702270607501759.PNG", true),
                            MemeImage.createMemeImage("Running away balloon", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/8251560209023487976.PNG", true),
                            MemeImage.createMemeImage("Scared cat", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/2477530499148820844.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(
                            MemeImage.createMemeImage("Silly cat", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/6315529842295112018.PNG", true),
                            MemeImage.createMemeImage("Awesome Awkward Penguin", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/8849463044200823824.PNG", true),
                            MemeImage.createMemeImage("Silly Peter Parker", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/5027783689869495966.PNG", true),
                            MemeImage.createMemeImage("Two buttons", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/8864323811965714575.PNG", true),
                            MemeImage.createMemeImage("Weird cat", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/5294591373114860185.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
                launch {
                    listOf(
                            MemeImage.createMemeImage("Woman yelling at a cat", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/3118701756182461752.PNG", true),
                            MemeImage.createMemeImage("x x everywhere", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/2789549287549790465.PNG", true),
                            MemeImage.createMemeImage("Young Cardi-B", "miscellaneous", "${MemeImage.memeTemplatesDirectory}/4051905074816386882.PNG", true),
                            MemeImage.createMemeImage("حمدي الوزير", "قبضه الهلالي", "${MemeImage.memeTemplatesDirectory}/7479651419850771894.PNG", true)
                    ).saveMultipleMemeObjectsToSharedPrefernces()
                }
            }.invokeOnCompletion {
                println("Download Completed = $currentSavedImages")
                updateDownloadStatusInSharedPrefrences()
                notifyDownloadObserver()
            }
        }

        override fun setContextObserver(activity: AppCompatActivity) {
            this.activity = activity

        }

        private fun List<MemeImage>.saveMultipleMemeObjectsToSharedPrefernces() {
            val sharedPreferences = getSharedPrefrences()
            val preferencesEditor = sharedPreferences.edit()
            this.forEach {
                val objectId = it.imageUri.replace("${MemeImage.memeTemplatesDirectory}/", "")
                preferencesEditor.putString(objectId, Gson().toJson(it)).apply()
                currentSavedImages++
            }
        }

        private fun getSharedPrefrences(stringResourceId: Int = R.string.meme_images_shared_prefrences_key): SharedPreferences {
            val context = activity.applicationContext
            val key = context.resources.getString(stringResourceId)
            return context.getSharedPreferences(key, Context.MODE_PRIVATE)
        }


        override fun setDownloadObserver(obj: Any) {
            downloadObserver = obj as DownloadObserver
        }

        override fun notifyDownloadObserver() {
            downloadObserver.updateDownloadObserver(currentSavedImages)
        }

        private fun updateDownloadStatusInSharedPrefrences() {
            val sharedPreferences = getSharedPrefrences(R.string.user_shared_prefrences)
            val sharedPrefrencesEditor = sharedPreferences.edit()
            val key = activity.resources.getString(R.string.has_previously_downloaded_images)
            sharedPrefrencesEditor.putBoolean(key, true).apply()
        }
    }
}