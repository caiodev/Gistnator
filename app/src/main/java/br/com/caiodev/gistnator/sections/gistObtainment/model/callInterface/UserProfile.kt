package br.com.caiodev.gistnator.sections.gistObtainment.model.callInterface

import br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes.Gist
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserProfile {

    /** Can happen sometimes https://github.com/square/okhttp/issues/3251 */

    @GET("gists/public")
    suspend fun provideGistListAsync(
        @Query("page") pageNumber: Int,
        @Query("per_page") maxQuantityPerPage: Int
    ): Response<List<Gist>>
}