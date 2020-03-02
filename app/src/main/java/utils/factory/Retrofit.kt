package utils.factory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object Retrofit {

    @PublishedApi
    internal val baseUrl = "https://api.github.com/"

    private const val timberTag = "OkHttp"

    private val httpLoggingInterceptor =
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag(timberTag).d(message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @PublishedApi
    internal inline fun <reified T> provideRetrofitService(): T = createRetrofitService()

    @PublishedApi
    internal inline fun <reified T> createRetrofitService() =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient())
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build().create(T::class.java) as T

    @PublishedApi
    internal fun provideOkHttpClient(): OkHttpClient = createOkHttpClient()

    private fun createOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
}