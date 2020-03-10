package br.com.caiodev.gistnator.sections.gistDetails.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GistDetails {

    @GET("gists/{id}")
    suspend fun provideGistDetailsAsync(@Path("id") gistId: String): Response<Gist>
}