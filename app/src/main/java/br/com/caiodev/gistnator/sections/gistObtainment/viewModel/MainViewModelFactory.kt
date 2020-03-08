package br.com.caiodev.gistnator.sections.gistObtainment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.caiodev.gistnator.sections.favoriteGists.model.diModules.favoriteGistRepository
import br.com.caiodev.gistnator.sections.gistObtainment.model.repository.GistListRepository
import utils.base.repository.remote.RemoteRepository
import utils.factory.Retrofit

class MainViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                GistListRepository(
                    RemoteRepository(),
                    Retrofit.provideRetrofitService()
                ), favoriteGistRepository
            ) as T
        }
        //noinspection unchecked
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}