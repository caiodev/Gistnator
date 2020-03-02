package br.com.caiodev.gistnator.sections.gistObtainment.model

import com.google.gson.annotations.SerializedName

data class MetaData(
    @SerializedName("filename") val fileName: String = "",
    @SerializedName("type") val type: String? = "",
    @SerializedName("language") val language: String? = "",
    @SerializedName("raw_url") val rawUrl: String = ""
)