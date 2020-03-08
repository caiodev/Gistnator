package br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database.queriesInterface

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import kotlinx.coroutines.flow.Flow

@Dao
interface GistDao {

    @Query("SELECT id, owner_image, first_name, file_description " + "FROM GistProperties")
    suspend fun obtainPaginatedGists(): List<GistProperties>

    @Insert
    suspend fun insertGistIntoTable(gistProperties: GistProperties)

    @Query("DELETE FROM GistProperties " + "WHERE id = :gistId")
    suspend fun deleteGist(gistId: String)
}