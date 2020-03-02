package utils.base.repository

import retrofit2.Response
import timber.log.Timber
import utils.constants.Constants.clientSideError
import utils.constants.Constants.connectException
import utils.constants.Constants.forbidden
import utils.constants.Constants.genericError
import utils.constants.Constants.genericException
import utils.constants.Constants.serverSideError
import utils.constants.Constants.socketTimeoutException
import utils.constants.Constants.sslHandshakeException
import utils.constants.Constants.successWithoutBody
import utils.constants.Constants.unknownHostException
import utils.service.APICallResult
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteRepository {

    suspend fun <T> callApi(
        call: suspend () -> Response<T>
    ): Any {

        try {

            val response = call.invoke()

            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    return APICallResult.Success(apiResponse)
                } ?: run {
                    return APICallResult.Success(successWithoutBody) // a.k.a 204 - No content
                }
            } else {

                Timber.d("ErrorCode: ${response.code()}")

                return when (response.code()) {
                    in 400..402, in 404..450 -> APICallResult.Error(clientSideError)
                    403 -> APICallResult.Error(forbidden)
                    in 500..598 -> APICallResult.Error(serverSideError)
                    else -> APICallResult.Error(genericError)
                }
            }
        } catch (exception: Exception) {
            return when (exception) {
                is ConnectException -> APICallResult.Error(connectException)
                is SocketTimeoutException -> APICallResult.Error(socketTimeoutException)
                is SSLHandshakeException -> APICallResult.Error(sslHandshakeException)
                is UnknownHostException -> APICallResult.Error(
                    unknownHostException
                )
                else -> APICallResult.Error(genericException)
            }
        }
    }
}