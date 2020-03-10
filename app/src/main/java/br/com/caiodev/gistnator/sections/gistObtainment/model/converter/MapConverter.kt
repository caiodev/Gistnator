package br.com.caiodev.gistnator.sections.gistObtainment.model.converter

import androidx.room.TypeConverter
import br.com.caiodev.gistnator.sections.gistObtainment.model.Metadata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): Map<String, Metadata> {
        val mapType = object : TypeToken<Map<String, Metadata>>(){}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromStringMap(map: Map<String, Metadata>): String = gson.toJson(map)
}