package br.com.caiodev.gistnator.sections.gistObtainment.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes.Gist
import br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes.Header
import br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders.GistViewHolder
import br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders.HeaderViewHolder
import br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders.transientItemViews.EndOfResultsViewHolder
import br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders.transientItemViews.LoadingViewHolder
import br.com.caiodev.gistnator.sections.gistObtainment.view.viewHolders.transientItemViews.RetryViewHolder
import utils.constants.Constants.gistCell
import utils.constants.Constants.header
import utils.constants.Constants.loading
import utils.constants.Constants.retry
import utils.interfaces.OnItemClicked
import utils.interfaces.viewTypes.ViewType

class GistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClicked: OnItemClicked
    private lateinit var dataSource: List<ViewType>

    override fun getItemCount() = dataSource.size

    override fun getItemViewType(position: Int) = itemViewType(position).provideViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)

        return when (viewType) {

            header -> HeaderViewHolder(
                itemView.inflate(
                    R.layout.header_view_holder_layout,
                    parent,
                    false
                )
            )

            gistCell -> GistViewHolder(
                itemView.inflate(
                    R.layout.gist_view_holder_layout,
                    parent,
                    false
                ), itemClicked
            )

            loading -> {
                LoadingViewHolder(
                    itemView.inflate(
                        R.layout.loading_view_holder_layout,
                        parent,
                        false
                    )
                )
            }

            retry -> {
                RetryViewHolder(
                    itemView.inflate(
                        R.layout.retry_view_holder_layout,
                        parent,
                        false
                    )
                    , itemClicked
                )
            }

            else -> EndOfResultsViewHolder(
                itemView.inflate(
                    R.layout.end_of_results_view_holder_layout,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is HeaderViewHolder -> viewHolder.bind(itemViewType(position) as Header)
            is GistViewHolder -> viewHolder.bind(itemViewType(position) as Gist)
        }
    }

    internal fun updateDataSource(newDataSource: List<ViewType>) {
        dataSource = newDataSource
    }

    private fun itemViewType(position: Int) = dataSource[position]

    internal fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }
}