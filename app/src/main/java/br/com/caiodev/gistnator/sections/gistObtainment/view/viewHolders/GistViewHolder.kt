package br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes.Gist
import kotlinx.android.synthetic.main.gist_view_holder_layout.view.*
import utils.constants.Constants.favorite
import utils.constants.Constants.gistCell
import utils.imageLoading.ImageLoader.loadImage
import utils.interfaces.OnItemClicked

class GistViewHolder(
    itemView: View,
    private val onItemClicked: OnItemClicked?
) :
    RecyclerView.ViewHolder(itemView) {

    init {

        itemView.parentLayout.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition, gistCell)
        }
    }

    fun bind(model: Gist) {

        changeFavoriteState(
            model,
            R.drawable.ic_favorite_border,
            R.drawable.ic_saved_favorite,
            {},
            {})

        itemView.favoriteImageView.setOnClickListener {
            changeFavoriteState(model,
                R.drawable.ic_saved_favorite,
                R.drawable.ic_favorite_border,
                { onItemClicked?.onItemClick(adapterPosition, favorite, false) },
                { onItemClicked?.onItemClick(adapterPosition, favorite, true) })
        }

        model.owner.login.let {
            itemView.userLogin.text = it
        }

        loadImage(
            model.owner.avatarUrl,
            R.mipmap.ic_launcher,
            itemView.userAvatar
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
            itemView.favoriteImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    nonFavoriteResource
                )
            )
            onNotSaved.invoke()
        } else {
            itemView.favoriteImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    favoriteResource
                )
            )
            onSaved.invoke()
        }
    }
}