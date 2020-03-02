package br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes.Header
import kotlinx.android.synthetic.main.header_view_holder_layout.view.*

class HeaderViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    internal fun bind(model: Header) {
        itemView.githubUsersListHeader.text =
            itemView.context.getString(model.headerName)
    }
}