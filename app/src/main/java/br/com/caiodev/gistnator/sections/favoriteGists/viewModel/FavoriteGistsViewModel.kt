package br.com.caiodev.gistnator.sections.favoriteGists.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.genericDatabase.GistDatabaseParentRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteGistsViewModel(private val repository: GistDatabaseParentRepository) : ViewModel() {

    val liveData = MutableLiveData<Any>()

    fun obtainPaginatedListOfGists() {
        viewModelScope.launch {
            val value = repository.obtainPaginatedGists()

            value.forEach {
                Timber.d("GistOwnerImages: ${it.ownerImage}")
            }

            liveData.postValue(value)
        }
    }

    fun deleteGist(gistId: String) {
        viewModelScope.launch {
            repository.deleteGist(gistId)

        }
    }
}