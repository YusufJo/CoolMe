package com.joseph.coolme.controller

import android.annotation.TargetApi
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.joseph.coolme.R
import com.joseph.coolme.model.MemeImage
import com.joseph.coolme.view.CardStackAdapter
import com.joseph.coolme.view.MemeImageDiffCallback
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_memes_detail.*
import java.util.ArrayList

class MemesDetailActivity : AppCompatActivity(), CardStackListener {
    private val cardStackView by lazy { card_stack_view as CardStackView }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(MemeImage.loadSavedMemeImages()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memes_detail)
        initializeViews()
        setupCardStackView()
        setupButtons()

    }

    private fun initializeViews(){
        TooltipCompat.setTooltipText(backup_button,"Backup files internally")
        TooltipCompat.setTooltipText(add_button,"Add meme image")
        TooltipCompat.setTooltipText(delete_button,"Delete meme image")
        layViewsUnderStatusBar()
        setStatusTextColorToDark()
    }


    private fun setupButtons(){
        onClickSaveBackup()
        onClickAddImageMemes()
        onClickDeleteMemeFromStorage()
    }

    private fun onClickSaveBackup() {
        backup_button.setOnClickListener {
            Toast.makeText(applicationContext, "Backup files were saved locally", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickAddImageMemes() {
        add_button.setOnClickListener {
        Toast.makeText(applicationContext,"Meme Was added", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickDeleteMemeFromStorage() {
        delete_button.setOnClickListener {
        Toast.makeText(applicationContext,"Meme was deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun layViewsUnderStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun setStatusTextColorToDark(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    //------------------ copied code -------------------//

//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawers()
//        } else {
//            super.onBackPressed()
//        }
//    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun setupCardStackView() {
        initialize()
    }


    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.1f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
        CardStackAdapter.setContextObserver(this)
    }


    private fun paginate() {
        val old = adapter.getMemeImages()
        val new = old.plus(MemeImage.loadSavedMemeImages())
        val callback = MemeImageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setMemeImages(new)
        result.dispatchUpdatesTo(adapter)
    }

}
