package br.com.caiodev.gistnator.sections.gistObtainment.model.koin

import br.com.caiodev.gistnator.sections.gistObtainment.model.repository.GistListRepository
import br.com.caiodev.gistnator.sections.gistObtainment.model.repository.GistRepository
import br.com.caiodev.gistnator.sections.gistObtainment.viewModel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import utils.base.repository.RemoteRepository
import utils.factory.Retrofit.provideRetrofitService

val gistObtainmentViewModel = module {
    viewModel { MainViewModel(repository = get()) }
    single { GistListRepository(RemoteRepository(), provideRetrofitService()) as GistRepository }
}