package utils.interfaces

import android.graphics.Bitmap

interface OnItemClicked {
    fun onItemClick(adapterPosition: Int, id: Int = 0, shouldBeDeleted: Boolean = false, itemImage: Bitmap? = null)
}