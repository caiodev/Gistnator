package br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes.Gist
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.gist_view_holder_layout.view.*
import kotlinx.android.synthetic.main.heart_component.view.*
import utils.constants.Constants.favorite
import utils.constants.Constants.gistCell
import utils.imageProcessing.ImageLoader.loadImage
import utils.interfaces.OnItemClicked

class GistViewHolder(
    itemView: View,
    private val onItemClicked: OnItemClicked?
) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var gist: Gist

    init {

        itemView.parentLayout.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition, gistCell)
        }

        itemView.saveFavoriteComponent.favoriteImageView.setOnClickListener {
            changeFavoriteState(gist,
                R.drawable.ic_saved_favorite,
                R.drawable.ic_favorite_border,
                {
                    onItemClicked?.onItemClick(
                        adapterPosition,
                        favorite,
                        false,
                        itemView.userAvatarImageView.drawable.toBitmap(200, 200)
                    )
                },
                {
                    onItemClicked?.onItemClick(
                        adapterPosition,
                        favorite,
                        true,
                        itemView.userAvatarImageView.drawable.toBitmap(200, 200)
                    )
                })
        }
    }

    fun bind(model: Gist) {

        gist = model

        changeFavoriteState(
            model,
            R.drawable.ic_favorite_border,
            R.drawable.ic_saved_favorite,
            {},
            {})

        model.owner.login.let {
            itemView.userLoginTextView.text = it
        }

        if (model.metaDataMap.size == 1) {
            model.metaDataMap.forEach {
                itemView.gistTypeTextView.text = it.value.type
            }
        } else
            itemView.gistTypeTextView.text = String.format(
                itemView.context.getString(R.string.gist_type),
                model.metaDataMap.size
            )

        loadImage(
            model.owner.avatarUrl,
            R.mipmap.ic_launcher,
            itemView.userAvatarImageView
        )
    }

    private fun changeFavoriteState(
        model: Gist,
        nonFavoriteResource: Int,
        favoriteResource: Int,
        onNotSaved: () -> Unit,
        onSaved: () -> Unit
    ) {

        if (!model.isSaved) {
            itemView.saveFavoriteComponent.favoriteImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    nonFavoriteResource
                )
            )
            onNotSaved.invoke()
        } else {
            itemView.saveFavoriteComponent.favoriteImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    favoriteResource
                )
            )
            onSaved.invoke()
        }
    }
}