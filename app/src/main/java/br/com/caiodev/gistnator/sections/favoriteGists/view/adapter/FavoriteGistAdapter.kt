package br.com.caiodev.gistnator.sections.favoriteGists.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.view.viewHolder.FavoriteGistViewHolder
import utils.interfaces.OnItemClicked

class FavoriteGistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClicked: OnItemClicked
    private var dataSource: List<GistProperties> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)

        return FavoriteGistViewHolder(
            itemView.inflate(
                R.layout.gist_view_holder_layout,
                parent,
                false
            ), itemClicked
        )
    }

    override fun getItemCount() = dataSource.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as FavoriteGistViewHolder).bind(dataSource[position])
    }

    internal fun updateDataSource(newDataSource: List<GistProperties>) {
        dataSource = newDataSource
    }

    internal fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }
}