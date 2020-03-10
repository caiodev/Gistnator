package br.com.caiodev.gistnator.sections.gistObtainment.model.repository

import br.com.caiodev.gistnator.sections.gistObtainment.model.callInterface.UserProfile
import utils.base.repository.remote.RemoteRepository

class GistListRepository(
    private val remoteRepository: RemoteRepository,
    private val retrofitService: UserProfile
) : GistRepository {

    override suspend fun provideGist(
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = remoteRepository.callApi(call = {
        retrofitService.provideGistListAsync(
            pageNumber,
            maxResultsPerPage
        )
    })
}