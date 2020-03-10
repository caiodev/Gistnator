package br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database.queriesInterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties

@Dao
interface GistDao {

    @Query("SELECT * " + "FROM GistProperties LIMIT 20")
    suspend fun obtainPaginatedGists(): List<GistProperties>

    @Insert
    suspend fun insertGistIntoTable(gistProperties: GistProperties)

    @Query("DELETE FROM GistProperties " + "WHERE id = :gistId")
    suspend fun deleteGist(gistId: String)
}