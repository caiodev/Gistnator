package br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes

import utils.constants.Constants.header
import utils.interfaces.viewTypes.ViewType

data class Header(val headerName: Int) : ViewType {
    override fun provideViewType() = header
}