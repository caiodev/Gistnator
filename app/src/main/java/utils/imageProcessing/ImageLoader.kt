package utils.imageProcessing

import android.widget.ImageView
import coil.api.load
import coil.request.CachePolicy

object ImageLoader {

    fun loadImage(
        imageUrl: String,
        placeholder: Int,
        targetImageView: ImageView,
        transformation: coil.transform.Transformation? = null
    ) {
        targetImageView.load(imageUrl) {
            crossfade(true)
            placeholder(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .error(placeholder).apply {
                    transformation?.let {
                        this.transformations(transformation)
                    }
                }
        }
    }
}