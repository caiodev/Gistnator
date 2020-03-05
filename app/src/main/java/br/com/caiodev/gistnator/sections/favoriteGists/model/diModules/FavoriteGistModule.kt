package br.com.caiodev.gistnator.sections.favoriteGists.model.diModules

import android.content.Context
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.GistDatabaseRepository
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database.GistDatabase
import br.com.caiodev.gistnator.sections.favoriteGists.viewModel.FavoriteGistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import utils.constants.Constants.databaseName
import utils.factory.Room

fun provideGistDatabaseInstance(context: Context) = module {
    viewModel { FavoriteGistsViewModel(repository = get()) }
    single {
        GistDatabaseRepository(
            Room.provideRoomInstance<GistDatabase>(
                context,
                databaseName
            )
        ) as br.com.caiodev.gistnator.sections.favoriteGists.model.repository.genericDatabase.GistDatabaseRepository
    }
}