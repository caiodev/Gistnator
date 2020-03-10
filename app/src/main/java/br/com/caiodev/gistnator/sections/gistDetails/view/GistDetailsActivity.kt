package br.com.caiodev.gistnator.sections.gistDetails.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.gistDetails.viewModel.GistDetailsViewModel
import br.com.caiodev.gistnator.sections.gistDetails.viewModel.GistDetailsViewModelFactory
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.activity_gist_details.*
import utils.base.flow.ViewFlow
import utils.constants.Constants.gistId
import utils.imageProcessing.ImageLoader.loadImage

class GistDetailsActivity : AppCompatActivity(R.layout.activity_gist_details), ViewFlow {

    private val viewModel: GistDetailsViewModel by lazy {
        ViewModelProvider(this, GistDetailsViewModelFactory()).get(GistDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
    }

    override fun setupView() {}

    override fun handleViewModel() {

        viewModel.gistDetailsMutableLiveData.observe(this, Observer {
            loadImage(
                it.owner.avatarUrl,
                R.drawable.ic_octocat,
                gistOwnerImageView,
                CircleCropTransformation()
            )
            gistOwnerName.text = it.owner.login
        })

        if (!viewModel.hasFirstSuccessfulCallBeenMade) {
            intent.getStringExtra(gistId)?.let {
                viewModel.provideGistDetails(it)
            }
        }
    }

    override fun setupExtras() {}
}