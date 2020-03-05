package br.com.caiodev.gistnator.sections.favoriteGists.model.repository.genericDatabase

import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties

interface GistDatabaseRepository {
    suspend fun obtainPaginatedGists(): List<GistProperties>
    suspend fun insertGistIntoTable(gistProperties: GistProperties)
    suspend fun deleteGist(gistProperties: GistProperties)
}