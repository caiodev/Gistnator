package br.com.caiodev.gistnator.sections.favoriteGists.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.favoriteGists.viewModel.FavoriteGistsViewModel
import br.com.caiodev.gistnator.sections.favoriteGists.viewModel.FavoriteGistsViewModelFactory
import utils.base.flow.ViewFlow

class FavoriteGistsActivity : AppCompatActivity(R.layout.activity_favorite_gists),
    ViewFlow {

    private val viewModel: FavoriteGistsViewModel by lazy {
        ViewModelProvider(
            this,
            FavoriteGistsViewModelFactory()
        ).get(FavoriteGistsViewModel::class.java)
    }

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

                }

                is List<*> ->
                    Toast.makeText(applicationContext, "QUERIED", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.obtainPaginatedListOfGists()
    }

    override fun setupExtras() {

    }
}