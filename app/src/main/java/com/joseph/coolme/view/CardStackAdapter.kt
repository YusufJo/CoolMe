package com.joseph.coolme.view

import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joseph.coolme.R
import com.joseph.coolme.model.ContextObservable
import com.joseph.coolme.model.MemeImage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.io.File

class CardStackAdapter(
        private var memeImages: List<MemeImage> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    companion object : ContextObservable {
        lateinit var activity: AppCompatActivity
        override fun setContextObserver(activity: AppCompatActivity) {
            Companion.activity = activity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memeImage = memeImages[position]
        holder.name.text = memeImage.name
        holder.category.text = memeImage.category
        Glide.with(holder.image)
                .load(memeImage.getBitmapImage)
                .into(holder.image)

        setupBlurryImage(holder, position)

    }

    override fun getItemCount(): Int {
        return memeImages.size
    }

    fun setMemeImages(memeImages: List<MemeImage>) {
        this.memeImages = memeImages
    }

    fun getMemeImages(): List<MemeImage> {
        return memeImages
    }

    fun clearMemeImages(){
        memeImages = listOf()
    }

    private fun setupBlurryImage(holder: ViewHolder, position: Int){
        Picasso.get().load(File(memeImages[position].imageUri)).transform(object : Transformation {
            override fun key(): String {
                return "blur"
            }

            val rs = RenderScript.create(activity.applicationContext)
            override fun transform(source: Bitmap?): Bitmap {
                val blurryBitmap = source?.copy(Bitmap.Config.ARGB_8888, true)
                val input = Allocation.createFromBitmap(rs, blurryBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED)
                val output = Allocation.createTyped(rs, input.type)
                val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                script.setInput(input)
                script.setRadius(20F)
                script.forEach(output)
                output.copyTo(blurryBitmap)
                source?.recycle()
                return blurryBitmap!!
            }
        }).into(holder.blurryImage)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var category: TextView = view.findViewById(R.id.item_category)
        var image: ImageView = view.findViewById(R.id.item_image)
        var blurryImage: ImageView = view.findViewById(R.id.item_blurry_image)
    }

}
