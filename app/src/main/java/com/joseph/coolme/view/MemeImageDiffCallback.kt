package com.joseph.coolme.view

import androidx.recyclerview.widget.DiffUtil
import com.joseph.coolme.model.MemeImage

class MemeImageDiffCallback(
        private val old: List<MemeImage>,
        private val new: List<MemeImage>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].name == new[newPosition].name
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
