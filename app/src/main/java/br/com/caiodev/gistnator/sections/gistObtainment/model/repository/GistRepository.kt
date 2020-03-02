package br.com.caiodev.gistnator.sections.gistObtainment.model.repository

interface GistRepository {
    suspend fun provideGist(
        pageNumber: Int = 0,
        maxResultsPerPage: Int = 0
    ): Any
}