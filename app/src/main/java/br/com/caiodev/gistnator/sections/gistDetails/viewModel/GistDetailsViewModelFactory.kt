package br.com.caiodev.gistnator.sections.gistDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.caiodev.gistnator.sections.gistDetails.model.GistDetailsRepository
import br.com.caiodev.gistnator.sections.gistObtainment.viewModel.MainViewModel
import utils.base.repository.remote.RemoteRepository
import utils.factory.Retrofit

class GistDetailsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GistDetailsViewModel::class.java)) {
            return GistDetailsViewModel(
                GistDetailsRepository(
                    RemoteRepository(),
                    Retrofit.provideRetrofitService()
                )
            ) as T
        }
        //noinspection unchecked
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}