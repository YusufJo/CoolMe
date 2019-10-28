package com.joseph.coolme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layViewsUnderStatusBar()

    }

    override fun onStart() {
        super.onStart()
        MemeImage.filesDir = filesDir
        MemeImage.writeObjectDirectoryOfMemes()
        MemeImage.writeImageDirectoryOfMemes()


    }

    override fun onResume() {
        super.onResume()
        if (MemeImage.objectsDirectory.listFiles()?.isEmpty()!!) {
            MemeImage.downloadDefaultMemesFromFirebase()

            val imageName = "7920923873988338478.PNG"
            val imageUri = "${MemeImage.templateFolderPath}/$imageName"
            MemeImage.createMemeImage(applicationContext, "بردو عاوز ايه", "احمد عبدالعزيز", "file://$imageUri", imageName, false)
        }

    }

//    override fun onResume() {
//        super.onResume()
//        runBlocking {
//            val stringPath = "android.resource://com.joseph.coolme/drawable"
//
//            launch { MemeImage.createMemeImage(applicationContext, "بردو عاوز ايه", "احمد عبدالعزيز", "$stringPath/a7md_3b3zez_bardo_3awez_eh") }
//            launch { MemeImage.createMemeImage(applicationContext, "يا بتاع نسوان", "ابو العربي", "$stringPath/abo_elaraby_ya_bta3_neswan") }
//            launch { MemeImage.createMemeImage(applicationContext, "حاولت اعمل حاجه صح", "الباشا تلميذ", "$stringPath/basha_telmeez") }
//            launch { MemeImage.createMemeImage(applicationContext, "Blank blue button", "miscellaneous", "$stringPath/blank_null_button") }
//            launch { MemeImage.createMemeImage(applicationContext, "ما تقول يا عم الحج", "بوحه", "$stringPath/bo7a_ma_t2ool_ya_3m_el_7g") }
//            launch { MemeImage.createMemeImage(applicationContext, "و انا بدبح الخاروف ٢", "بوشكاش", "$stringPath/boashkash_w_ana_bdb7_el_5aroof_2") }
//            launch { MemeImage.createMemeImage(applicationContext, "و انا بدبح الخاروف ١", "بوشكاش", "$stringPath/boshkash_w_ana_bdb7_el5aroof") }
//            launch { MemeImage.createMemeImage(applicationContext, "Cat taking selfie", "miscellaneous", "$stringPath/cat_taking_selfie") }
//            launch { MemeImage.createMemeImage(applicationContext, "Computer guy", "miscellaneous", "$stringPath/computer_guy") }
//            launch { MemeImage.createMemeImage(applicationContext, "Crying cat 1", "miscellaneous", "$stringPath/crying_cat_1") }
//            launch { MemeImage.createMemeImage(applicationContext, "Distracted boyfriend", "miscellaneous", "$stringPath/distracted_boyfriend") }
//            launch { MemeImage.createMemeImage(applicationContext, "Don't you Squidward", "miscellaneous", "$stringPath/dont_you_squidward") }
//            launch { MemeImage.createMemeImage(applicationContext, "Hotline bling", "miscellaneous", "$stringPath/drake_hotline_bling") }
//            launch { MemeImage.createMemeImage(applicationContext, "كان نفسي اقولك", "الباشا تلميذ", "$stringPath/el_basha_telmeez_kan_nefsy_a2olak") }
//            launch { MemeImage.createMemeImage(applicationContext, "مش ده اللي متجوز البنت الخبره", "التجربه الدنماركيه", "$stringPath/el_tagroba_el_denimarkeya") }
//            launch { MemeImage.createMemeImage(applicationContext, "Finding neverland", "miscellaneous", "$stringPath/finding_neverland") }
//            launch { MemeImage.createMemeImage(applicationContext, "Gaven Shook 2", "miscellaneous", "$stringPath/gaven_shook_2") }
//            launch { MemeImage.createMemeImage(applicationContext, "Gaven Shook 1", "miscellaneous", "$stringPath/gavin_shook") }
//            launch { MemeImage.createMemeImage(applicationContext, "Hamdi el-Wazeer cat", "miscellaneous", "$stringPath/hamdy_elwazeer_cat") }
//            launch { MemeImage.createMemeImage(applicationContext, "I Know That feeling Bro", "miscellaneous", "$stringPath/i_know_that_feel_bro") }
//            launch { MemeImage.createMemeImage(applicationContext, "Shaking face", "miscellaneous", "$stringPath/shaky_face") }
//            launch { MemeImage.createMemeImage(applicationContext, "Imagination Spongebob", "miscellaneous", "$stringPath/imagination_spongebob") }
//            launch { MemeImage.createMemeImage(applicationContext, "Is This a pigeon", "miscellaneous", "$stringPath/is_this_a_pigeon") }
//            launch { MemeImage.createMemeImage(applicationContext, "البنت الغلسه", "الليمبي", "$stringPath/lemby_bent_8elsa") }
//            launch { MemeImage.createMemeImage(applicationContext, "Leonardo Dicaprio cheers", "miscellaneous", "$stringPath/leonardo_dicaprio_cheers") }
//            launch { MemeImage.createMemeImage(applicationContext, "Little sad kid 1", "miscellaneous", "$stringPath/little_sad_kid_1") }
//            launch { MemeImage.createMemeImage(applicationContext, "Little sad kid 2", "miscellaneous", "$stringPath/little_sad_kid_2") }
//            launch { MemeImage.createMemeImage(applicationContext, "Little sad kid 3", "miscellaneous", "$stringPath/little_sad_kid_3") }
//            launch { MemeImage.createMemeImage(applicationContext, "Little sad kid 4", "miscellaneous", "$stringPath/little_sad_kid_4") }
//            launch { MemeImage.createMemeImage(applicationContext, "LOL rage face", "miscellaneous", "$stringPath/lol_guy") }
//            launch { MemeImage.createMemeImage(applicationContext, "كنت يهودي عبقري", "محي اسماعيل", "$stringPath/mo7y_esmail_kont_yahoody_3bkry") }
//            launch { MemeImage.createMemeImage(applicationContext, "Okay rage face", "miscellaneous", "$stringPath/okay_guy_rage_face2") }
//            launch { MemeImage.createMemeImage(applicationContext, "Question rage face", "miscellaneous", "$stringPath/question_rage_face") }
//            launch { MemeImage.createMemeImage(applicationContext, "Running away balloon", "miscellaneous", "$stringPath/running_away_balloon") }
//            launch { MemeImage.createMemeImage(applicationContext, "Scared cat", "miscellaneous", "$stringPath/scared_cat") }
//            launch { MemeImage.createMemeImage(applicationContext, "Silly cat", "miscellaneous", "$stringPath/silly_cat") }
//            launch { MemeImage.createMemeImage(applicationContext, "Awesome Awkward Penguin", "miscellaneous", "$stringPath/socially_awesome_awkward_penguin") }
//            launch { MemeImage.createMemeImage(applicationContext, "Silly Peter Parker", "miscellaneous", "$stringPath/spiderman_peter_parker") }
//            launch { MemeImage.createMemeImage(applicationContext, "Two buttons", "miscellaneous", "$stringPath/two_buttons") }
//            launch { MemeImage.createMemeImage(applicationContext, "Weird cat", "miscellaneous", "$stringPath/weird_cat") }
//            launch { MemeImage.createMemeImage(applicationContext, "Woman yelling at a cat", "miscellaneous", "$stringPath/woman_yelling_at_a_cat") }
//            launch { MemeImage.createMemeImage(applicationContext, "x x everywhere", "miscellaneous", "$stringPath/x_x_everywhere") }
//            launch { MemeImage.createMemeImage(applicationContext, "Young Cardi-B", "احمد عبدالعزيز", "$stringPath/young_cardi_b") }
//            launch { MemeImage.createMemeImage(applicationContext, "حمدي الوزير", "قبضه الهلالي", "$stringPath/hamdey_elwazeer") }
//        }
//    }

    fun onClickStartMemesActivity(view: View) {
        val intent = Intent(this, MemesActivity::class.java)
        startActivity(intent)
    }

    fun onClickStartGifsActivity(view: View) {
        val intent = Intent(this, GifsActivity::class.java)
        startActivity(intent)
    }


    private fun layViewsUnderStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

}
