package br.com.caiodev.gistnator.sections.favoriteGists.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.viewModel.FavoriteGistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.base.flow.ViewFlow

class FavoriteGistsActivity : AppCompatActivity(R.layout.activity_favorite_gists),
    ViewFlow {

    private val viewModel by viewModel<FavoriteGistsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

    override fun setupView() {

    }

    override fun handleViewModel() {

        viewModel.liveData.observe(this, Observer { value ->

            when (value) {

                is Int -> {
                    Toast.makeText(applicationContext, "INSERTED", Toast.LENGTH_LONG).show()
                    viewModel.obtainPaginatedGists()
                }

                is List<*> ->
                    Toast.makeText(applicationContext, "QUERIED", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.insertGistIntoTable(
            GistProperties(
                "an8545846654dfdfa",
                "dafdfg456s4g65sdf4g",
                "jason",
                "arquivo_zuado"
            )
        )
    }

    override fun setupExtras() {

    }
}