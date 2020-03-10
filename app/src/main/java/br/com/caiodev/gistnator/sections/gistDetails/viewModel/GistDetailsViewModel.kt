package br.com.caiodev.gistnator.sections.gistDetails.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiodev.gistnator.sections.gistDetails.model.GistDetailsGenericRepository
import kotlinx.coroutines.launch
import utils.service.APICallResult

class GistDetailsViewModel(private val gistDetailsRepository: GistDetailsGenericRepository) :
    ViewModel() {

    internal val gistDetailsMutableLiveData =
        MutableLiveData<br.com.caiodev.gistnator.sections.gistDetails.model.Gist>()

    //Call related call flag
    internal var hasFirstSuccessfulCallBeenMade = false

    fun provideGistDetails(gistId: String) {

        viewModelScope.launch {

            when (val value = gistDetailsRepository.provideGistDetails(gistId)) {

                is APICallResult.Success<*> -> {
                    hasFirstSuccessfulCallBeenMade = true
                    (value.data as br.com.caiodev.gistnator.sections.gistDetails.model.Gist).apply {
                        gistDetailsMutableLiveData.postValue(this)
                    }
                }

                is APICallResult.Error -> {

                }
            }
        }
    }
}