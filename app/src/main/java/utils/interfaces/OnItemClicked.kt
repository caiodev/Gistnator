package utils.interfaces

import android.graphics.Bitmap

interface OnItemClicked {
    fun onItemClick(adapterPosition: Int, id: Int, shouldBeDeleted: Boolean = false, itemImage: Bitmap? = null)
}