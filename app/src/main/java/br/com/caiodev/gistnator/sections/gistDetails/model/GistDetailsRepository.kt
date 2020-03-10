package br.com.caiodev.gistnator.sections.gistDetails.model

import utils.base.repository.remote.RemoteRepository

class GistDetailsRepository(
    private val remoteRepository: RemoteRepository,
    private val retrofitService: GistDetails
) : GistDetailsGenericRepository {

    override suspend fun provideGistDetails(gistId: String) = remoteRepository.callApi(call = {
        retrofitService.provideGistDetailsAsync(
            gistId
        )
    })
}