package br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes

import br.com.caiodev.gistnator.sections.gistObtainment.model.Metadata
import br.com.caiodev.gistnator.sections.gistObtainment.model.Owner
import com.google.gson.annotations.SerializedName
import utils.constants.Constants.gistCell
import utils.interfaces.viewTypes.ViewType

data class Gist(
    @SerializedName("id") val id: String = "",
    @SerializedName("files") val metaDataMap: Map<String, Metadata>,
    @SerializedName("owner") val owner: Owner = Owner(),
    var isSaved: Boolean = false
) : ViewType {
    override fun provideViewType() = gistCell
}