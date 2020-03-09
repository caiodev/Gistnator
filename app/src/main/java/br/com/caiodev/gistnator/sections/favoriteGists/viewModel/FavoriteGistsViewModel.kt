package br.com.caiodev.gistnator.sections.favoriteGists.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.genericDatabase.GistDatabaseParentRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteGistsViewModel(private val repository: GistDatabaseParentRepository) : ViewModel() {

    val liveData = MutableLiveData<List<GistProperties>>()

    private var favoriteGistList = mutableListOf<GistProperties>()

    fun obtainPaginatedListOfGists() {

        viewModelScope.launch {

            val value = repository.obtainPaginatedGists()

            favoriteGistList.clear()
            favoriteGistList.addAll(value)
            liveData.postValue(favoriteGistList.toList())
        }
    }

    fun deleteGist(index: Int) {
        viewModelScope.launch {
            repository.deleteGist(favoriteGistList[index].id)
            obtainPaginatedListOfGists()
        }
    }
}