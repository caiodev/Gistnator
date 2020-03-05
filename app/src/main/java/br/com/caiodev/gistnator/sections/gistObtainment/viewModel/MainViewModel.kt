package br.com.caiodev.gistnator.sections.gistObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.gistObtainment.model.repository.GistRepository
import br.com.caiodev.gistnator.sections.gistObtainment.model.viewTypes.*
import kotlinx.coroutines.launch
import utils.constants.Constants.clientSideError
import utils.constants.Constants.connectException
import utils.constants.Constants.endOfResults
import utils.constants.Constants.forbidden
import utils.constants.Constants.loading
import utils.constants.Constants.numberOfItemsPerPage
import utils.constants.Constants.retry
import utils.constants.Constants.serverSideError
import utils.constants.Constants.socketTimeoutException
import utils.constants.Constants.sslHandshakeException
import utils.constants.Constants.unknownHostException
import utils.extensions.dropLast
import utils.extensions.toImmutableSingleLiveEvent
import utils.interfaces.viewTypes.ViewType
import utils.liveEvent.SingleLiveEvent
import utils.service.APICallResult

class MainViewModel(
    private val repository:
    GistRepository
) : ViewModel() {

    //Success LiveDatas
    private val mainListMutableLiveData = MutableLiveData<List<ViewType>>()
    internal val mainListLiveData: LiveData<List<ViewType>>
        get() = mainListMutableLiveData

    //Error LiveDatas
    private val errorSingleMutableLiveDataEvent =
        SingleLiveEvent<Int>()
    internal val errorSingleImmutableLiveDataEvent: LiveData<Int>
        get() = errorSingleMutableLiveDataEvent.toImmutableSingleLiveEvent()

    //Result list
    private val gistMutableList = mutableListOf<ViewType>()

    //Information cache variables
    internal var lastVisibleListItem = 0

    //Flags
    private var pageNumber = 1
    internal var hasForbiddenErrorBeenEmitted = false

    //Call related flags
    internal var hasFirstSuccessfulCallBeenMade = false
    internal var isThereAnOngoingCall = false
    internal var hasUserTriggeredANewRequest = false

    internal var hasAnyUserRequestedUpdatedData = false
    internal var shouldASearchBePerformed = true

    internal var isTheNumberOfItemsOfTheLastCallLessThanTwenty = false

    //Transient list item view flags
    private var isEndOfResultsListItemVisible = false
    private var isPaginationLoadingListItemVisible = false
    internal var isRetryListItemVisible = false

    internal fun requestUpdatedGithubProfiles() {
        hasAnyUserRequestedUpdatedData = true
        pageNumber = 1
        requestGithubProfiles(true)
    }

    internal fun requestMoreGithubProfiles() {
        requestGithubProfiles(false)
    }

    //This method is where all the utils.utils.network request process starts. First, when it is called,
    private fun requestGithubProfiles(
        shouldListItemsBeRemoved: Boolean
    ) {

        isThereAnOngoingCall = true

        viewModelScope.launch {
            handleCallSuccess(shouldListItemsBeRemoved)
        }
    }

    //This method handles both Success an Error states and delivers the result through a LiveData post to the view. Which in this case is GithubProfileInfoObtainmentActivity
    @Suppress("UNCHECKED_CAST")
    private suspend fun handleCallSuccess(
        shouldListItemsBeRemoved: Boolean = false
    ) {

        if (!hasUserTriggeredANewRequest) insertTransientItemIntoTheResultsList(loading, true)

        when (val value =
            repository.provideGist(
                pageNumber,
                numberOfItemsPerPage
            )) {

            //Success state handling
            is APICallResult.Success<*> -> {
                isThereAnOngoingCall = false
                hasForbiddenErrorBeenEmitted = false
                with(value.data as List<Gist>) {

                    //                    this.flatMap<List<GistDatabaseParentRepository>, GistDatabaseParentRepository> { gist ->
//                        gist.metaDataMap.forEach { metaDataMap ->
//                            Timber.d("FileType: ${metaDataMap.value.type}")
//                        }
//                    }

                    isTheNumberOfItemsOfTheLastCallLessThanTwenty =
                        this.size < 20

                    if (shouldListItemsBeRemoved) {
                        setupList(this)
                        if (hasLastCallBeenSuccessful()) hasFirstSuccessfulCallBeenMade = true
                    } else {
                        if (hasLastCallBeenSuccessful() && isPaginationLoadingListItemVisible) {
                            dropLast()
                        }

                        gistMutableList.addAll(this)

                        if (isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
                            endOfResults, true
                        )

                        mainListMutableLiveData.postValue(gistMutableList.toList())
                    }
                    pageNumber++
                }
            }

            else -> handleErrorResult(value as APICallResult.Error)
        }
    }

    private fun handleErrorResult(errorValue: APICallResult.Error) {

        //Error state handling
        isThereAnOngoingCall = false
        hasAnyUserRequestedUpdatedData = false
        hasForbiddenErrorBeenEmitted = false

        insertTransientItemIntoTheResultsList(retry, true)

        with(errorSingleMutableLiveDataEvent) {

            when (errorValue.error) {

                unknownHostException, socketTimeoutException, connectException -> errorPairProvider(
                    R.string.unknown_host_exception_and_socket_timeout_exception,
                    this
                )

                sslHandshakeException -> errorPairProvider(
                    R.string.ssl_handshake_exception,
                    this
                )

                clientSideError -> errorPairProvider(
                    R.string.client_side_error,
                    this
                )

                serverSideError -> errorPairProvider(
                    R.string.server_side_error,
                    this
                )

                forbidden -> {
                    hasForbiddenErrorBeenEmitted = true
                    errorPairProvider(
                        R.string.api_query_limit_exceeded_error,
                        this
                    )
                }

                else -> errorPairProvider(
                    R.string.generic_exception_and_generic_error,
                    this
                )
            }
        }

    }

    /* This method sets up the list with all default RecyclerViewItems it will need. In the first call, a Header is added at the top of the list and following it,
    the ProfileInformation item is added which is responsible for showing the searched user related data */
    private fun setupList(
        githubUserInformationList: List<Gist>
    ) {
        gistMutableList.clear()
        isPaginationLoadingListItemVisible = false
        isRetryListItemVisible = false
        isEndOfResultsListItemVisible = false
        gistMutableList.add(Header(R.string.github_user_list_header))
        githubUserInformationList.forEach {
            populateList(it)
        }

        if (isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
            endOfResults
        )
        mainListMutableLiveData.postValue(gistMutableList.toList())
    }

    //This method populates the GithubUserProfile related information list which in this case is githubProfilesInfoMutableList
    private fun populateList(githubInfo: Gist) {
        gistMutableList.add(
            Gist(
                githubInfo.id,
                githubInfo.metaDataMap,
                githubInfo.owner
            )
        )
    }

    private fun errorPairProvider(
        error: Int,
        state: SingleLiveEvent<Int>
    ) {
        state.postValue(error)
    }

    private fun insertTransientItemIntoTheResultsList(
        state: Int,
        shouldPostValue: Boolean = false
    ) {

        if (hasLastCallBeenSuccessful()) {

            when (state) {

                loading -> {
                    isThereAnOngoingCall = true
                    if (isRetryListItemVisible) dropLast()
                    gistMutableList.add(Loading())
                    isPaginationLoadingListItemVisible = true
                    isRetryListItemVisible = false
                    isEndOfResultsListItemVisible = false
                }

                retry -> {
                    if (isPaginationLoadingListItemVisible) dropLast()
                    gistMutableList.add(Retry())
                    isRetryListItemVisible = true
                    isPaginationLoadingListItemVisible = false
                    isEndOfResultsListItemVisible = false
                }

                else -> {
                    if (isPaginationLoadingListItemVisible || isRetryListItemVisible) dropLast()
                    gistMutableList.add(EndOfResults())
                    isEndOfResultsListItemVisible = true
                    isPaginationLoadingListItemVisible = false
                    isRetryListItemVisible = false
                }
            }
        }

        if (shouldPostValue) mainListMutableLiveData.postValue(gistMutableList.toList())
    }

    private fun dropLast() {
        dropLast(gistMutableList)
    }

    private fun hasLastCallBeenSuccessful() = gistMutableList.isNotEmpty()

    internal fun saveGist(listPosition: Int) {
        (gistMutableList[listPosition] as Gist).isSaved = true
        mainListMutableLiveData.postValue(gistMutableList.toList())
    }

    internal fun deleteGist(listPosition: Int) {
        (gistMutableList[listPosition] as Gist).isSaved = false
        mainListMutableLiveData.postValue(gistMutableList.toList())
    }

    //This method provides a URL to the profile a user clicks on a List item
    internal fun provideGistId(index: Int) =
        (gistMutableList[index] as Gist).id
}