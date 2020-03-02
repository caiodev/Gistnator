package br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders.transientItemViews

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.retry_view_holder_layout.view.*
import utils.constants.Constants.retry
import utils.interfaces.OnItemClicked

class RetryViewHolder(itemView: View, private val onItemClicked: OnItemClicked?) :
    RecyclerView.ViewHolder(itemView) {

    init {

        itemView.retryTextViewParentLayout.retryTextView.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition, retry)
        }
    }
}