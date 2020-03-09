package br.com.caiodev.gistnator.sections.favoriteGists.view.viewHolder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import kotlinx.android.synthetic.main.gist_view_holder_layout.view.*
import kotlinx.android.synthetic.main.heart_component.view.*
import utils.interfaces.OnItemClicked

class FavoriteGistViewHolder(
    itemView: View,
    private val onItemClicked: OnItemClicked?
) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var gistProperties: GistProperties

    init {
        itemView.saveFavoriteComponent.favoriteImageView.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition)
        }
    }

    fun bind(model: GistProperties) {

        gistProperties = model

        itemView.saveFavoriteComponent.favoriteImageView.setImageDrawable(
            ContextCompat.getDrawable(
                itemView.context,
                R.drawable.ic_saved_favorite
            )
        )

        itemView.userLoginTextView.text = model.ownerName
    }
}