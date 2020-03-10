package br.com.caiodev.gistnator.sections.favoriteGists.model.diModules

import android.content.Context
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.GistDatabaseRepository
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database.GistDatabase
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.genericDatabase.GistDatabaseParentRepository
import utils.constants.Constants.databaseName
import utils.factory.Room

lateinit var favoriteGistRepository: GistDatabaseParentRepository

fun provideGistDatabaseInstance(context: Context) {
    favoriteGistRepository = GistDatabaseRepository(
        Room.provideRoomInstance<GistDatabase>(
            context,
            databaseName
        )
    )
}