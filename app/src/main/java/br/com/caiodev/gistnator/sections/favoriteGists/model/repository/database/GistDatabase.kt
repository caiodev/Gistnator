package br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.caiodev.gistnator.sections.favoriteGists.model.GistProperties
import br.com.caiodev.gistnator.sections.favoriteGists.model.repository.database.queriesInterface.GistDao
import br.com.caiodev.gistnator.sections.gistObtainment.model.converter.MapConverter

@Database(entities = [GistProperties::class], version = 1)
@TypeConverters(MapConverter::class)
abstract class GistDatabase : RoomDatabase() {
    abstract fun provideDao(): GistDao
}