package br.com.caiodev.gistnator.sections.gistDetails.model

interface GistDetailsGenericRepository {

    suspend fun provideGistDetails(
        gistId: String
    ): Any
}