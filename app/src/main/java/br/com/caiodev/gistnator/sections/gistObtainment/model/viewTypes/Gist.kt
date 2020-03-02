package br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes

import br.com.caiodev.gistnator.sections.gistObtainment.model.Files
import br.com.caiodev.gistnator.sections.gistObtainment.model.Owner
import com.google.gson.annotations.SerializedName
import utils.constants.Constants.gistCell
import utils.interfaces.viewTypes.ViewType

data class Gist(
    @SerializedName("url") val url: String = "",
    @SerializedName("files") val files: Files = Files(mapOf()),
    @SerializedName("owner") val owner: Owner = Owner()
) : ViewType {
    override fun provideViewType() = gistCell
}