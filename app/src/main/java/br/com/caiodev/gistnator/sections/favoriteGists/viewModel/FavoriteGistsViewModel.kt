package br.com.caiodev.gistnator.sections.favoriteGists.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.genericDatabase.GistDatabaseRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteGistsViewModel(private val repository: GistDatabaseRepository) : ViewModel() {

    val liveData = MutableLiveData<Any>()

    fun obtainPaginatedGists() {
        viewModelScope.launch {
            val value = repository.obtainPaginatedGists()
            Timber.d("RoomGist: ${value[0].id}")
            liveData.postValue(value)
        }
    }

    fun insertGistIntoTable(gistProperties: GistProperties) {
        viewModelScope.launch {
            repository.insertGistIntoTable(gistProperties)
            liveData.postValue(123)
        }
    }

    fun deleteGist(gistProperties: GistProperties) {
        viewModelScope.launch {
            repository.deleteGist(gistProperties)
        }
    }
}