package br.com.caiodev.gistnator.sections.gistDetails.model

import br.com.caiodev.gistnator.sections.gistObtainment.model.Metadata
import br.com.caiodev.gistnator.sections.gistObtainment.model.Owner
import com.google.gson.annotations.SerializedName

data class Gist(
    @SerializedName("id") val id: String = "",
    @SerializedName("files") val metaDataMap: Map<String, Metadata>,
    @SerializedName("owner") val owner: Owner = Owner()
)