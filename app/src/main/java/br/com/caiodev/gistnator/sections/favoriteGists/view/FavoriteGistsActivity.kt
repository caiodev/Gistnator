package br.com.caiodev.gistnator.sections.favoriteGists.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.favoriteGists.view.adapter.FavoriteGistAdapter
import br.com.caiodev.gistnator.sections.favoriteGists.viewModel.FavoriteGistsViewModel
import br.com.caiodev.gistnator.sections.favoriteGists.viewModel.FavoriteGistsViewModelFactory
import br.com.caiodev.gistnator.sections.gistDetails.view.GistDetailsActivity
import kotlinx.android.synthetic.main.activity_favorite_gists.*
import utils.base.flow.ViewFlow
import utils.constants.Constants.favorite
import utils.constants.Constants.gistCell
import utils.constants.Constants.gistId
import utils.extensions.showInternetConnectionStatusSnackBar
import utils.extensions.showSnackBar
import utils.interfaces.OnItemClicked
import utils.network.NetworkChecking
import utils.snackBar.CustomSnackBar

class FavoriteGistsActivity : AppCompatActivity(R.layout.activity_favorite_gists),
    ViewFlow {

    private var shouldExecuteAnimation = true
    private var currentAdapterPosition = 0

    private lateinit var customSnackBar: CustomSnackBar

    private val viewModel: FavoriteGistsViewModel by lazy {
        ViewModelProvider(
            this,
            FavoriteGistsViewModelFactory()
        ).get(FavoriteGistsViewModel::class.java)
    }

    private val favoriteGistAdapter: FavoriteGistAdapter by lazy {
        FavoriteGistAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

    override fun setupView() {

        customSnackBar = CustomSnackBar.make(this.findViewById(android.R.id.content))

        favoriteGistRecyclerView.apply {
            setHasFixedSize(true)
            adapter = favoriteGistAdapter
        }

        favoriteGistAdapter.setOnItemClicked(object : OnItemClicked {
            override fun onItemClick(
                adapterPosition: Int,
                id: Int,
                shouldBeDeleted: Boolean,
                itemImage: Bitmap?
            ) {

                when (id) {

                    gistCell -> {
                        checkIfInternetConnectionIsAvailableCaller(
                            onConnectionAvailable = {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        GistDetailsActivity::class.java
                                    ).putExtra(
                                        gistId,
                                        viewModel.provideFavoriteGistId(
                                            adapterPosition
                                        )
                                    )
                                )
                            },
                            onConnectionUnavailable = {
                                showInternetConnectionStatusSnackBar(
                                    applicationContext,
                                    customSnackBar,
                                    false
                                )
                            })
                    }

                    favorite -> {
                        currentAdapterPosition = adapterPosition
                        shouldExecuteAnimation = false
                        viewModel.deleteGist(adapterPosition)
                    }
                }
            }
        })
    }

    override fun handleViewModel() {

        viewModel.liveData.observe(this, Observer { value ->

            favoriteGistAdapter.updateDataSource(value)

            if (shouldExecuteAnimation)
                runLayoutAnimation(favoriteGistRecyclerView)
            else {
                shouldExecuteAnimation = true
                favoriteGistRecyclerView.adapter?.notifyItemRemoved(currentAdapterPosition)
                showSnackBar(this, "Gist removed") {}
                favoriteGistRecyclerView.adapter?.notifyDataSetChanged()
            }
        })

        viewModel.obtainPaginatedListOfGists()
    }

    override fun setupExtras() {}

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        with(recyclerView) {
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }

    private fun checkIfInternetConnectionIsAvailableCaller(
        onConnectionAvailable: () -> Unit,
        onConnectionUnavailable: () -> Unit
    ) {
        NetworkChecking.checkIfInternetConnectionIsAvailable(
            applicationContext,
            onConnectionAvailable,
            onConnectionUnavailable
        )
    }
}