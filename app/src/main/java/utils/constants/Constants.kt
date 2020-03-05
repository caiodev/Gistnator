package utils.constants

object Constants {

    const val gistId = "gistId"

    //RecyclerView view types
    const val header = 0
    const val gistCell = 1
    const val favorite = 2

    //Subtypes of Generic
    const val loading = 2
    const val retry = 3
    const val endOfResults = 4

    //Call states
    //Success
    const val successWithoutBody = 1

    //Error
    //Exceptions
    const val connectException = 2
    const val genericException = 3
    const val socketTimeoutException = 4
    const val sslHandshakeException = 5
    const val unknownHostException = 6

    //HTTP codes
    const val clientSideError = 7
    const val serverSideError = 8
    const val forbidden = 9

    //Generic
    const val genericError = 10

    //Pagination
    const val numberOfItemsPerPage = 20

    //Database
    const val databaseName = "gist_database"
}