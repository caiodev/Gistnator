package utils.imageLoading

import android.widget.ImageView
import coil.api.load
import coil.request.CachePolicy

object ImageLoader {

    fun loadImage(imageUrl: String, placeholder: Int, targetImageView: ImageView) {
        targetImageView.load(imageUrl) {
            crossfade(true)
            placeholder(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .error(placeholder)
        }
    }
}