package br.com.caiodev.gistnator.sections.favoriteGists.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import br.com.caiodev.gistnator.sections.gistObtainment.model.Metadata
import br.com.caiodev.gistnator.sections.gistObtainment.model.converter.MapConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class GistProperties(
    @PrimaryKey val id: String = "",
    @ColumnInfo(name = "owner_image") val ownerImage: String = "",
    @ColumnInfo(name = "first_name") val ownerName: String = "",
    @ColumnInfo(name = "file_description")
    val fileDescription: Map<String, Metadata> = HashMap()
)