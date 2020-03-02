package br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders

import android.view.View
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

        itemView.favoriteCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                onItemClicked?.onItemClick(adapterPosition, favorite)
        }
    }

    fun bind(model: Gist) {

        model.owner.login.let {
            itemView.userLogin.text = it
        }

        loadImage(
            model.owner.avatarUrl,
            R.mipmap.ic_launcher,
            itemView.userAvatar
        )
    }
}