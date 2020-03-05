package br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database.queriesInterface.GistDao

@Database(entities = [GistProperties::class], version = 1)
abstract class GistDatabase : RoomDatabase() {
    abstract fun provideDao(): GistDao
}