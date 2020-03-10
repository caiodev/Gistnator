package br.com.caiodev.gistnator.sections.favoriteGists.model.repository

import androidx.room.RoomDatabase
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database.GistDatabase
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.genericDatabase.GistDatabaseParentRepository

class GistDatabaseRepository(database: RoomDatabase) : GistDatabaseParentRepository {

    private val gistDatabase = (database as GistDatabase).provideDao()

    override suspend fun obtainPaginatedGists() =
        gistDatabase.obtainPaginatedGists()

    override suspend fun insertGistIntoTable(gistProperties: GistProperties) {
        gistDatabase.insertGistIntoTable(gistProperties)
    }

    override suspend fun deleteGist(gistId: String) {
        gistDatabase.deleteGist(gistId)
    }
}