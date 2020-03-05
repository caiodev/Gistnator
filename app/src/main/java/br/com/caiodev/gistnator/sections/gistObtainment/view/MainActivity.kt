package br.com.caiodev.gistnator.sections.gistObtainment.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.favoriteGists.view.FavoriteGistsActivity
import br.com.caiodev.gistnator.sections.gistObtainment.model.adapter.GistAdapter
import br.com.caiodev.gistnator.sections.gistObtainment.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.base.flow.ViewFlow
import utils.constants.Constants.favorite
import utils.constants.Constants.gistCell
import utils.constants.Constants.retry
import utils.extensions.applyViewVisibility
import utils.extensions.showSnackBar
import utils.interfaces.OnItemClicked
import utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import utils.snackBar.CustomSnackBar

class MainActivity :
    AppCompatActivity(R.layout.activity_main),
    ViewFlow {

    private var shouldRecyclerViewAnimationBeExecuted = true
    private var hasBackToTopButtonBeenClicked = false
    private lateinit var countingIdlingResource: CountingIdlingResource

    private lateinit var customSnackBar: CustomSnackBar

    private val viewModel by viewModel<MainViewModel>()

    private val gistAdapter by lazy {
        GistAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
        performCall()
    }

    private fun performCall() {
        if (!viewModel.hasFirstSuccessfulCallBeenMade)
            callApiThroughViewModel { viewModel.requestUpdatedGithubProfiles() }
    }

    override fun setupView() {

        //Condition when users rotate the screen and the activity gets destroyed
        if (viewModel.isThereAnOngoingCall) {
            applyViewVisibility(repositoryLoadingProgressBar, View.VISIBLE)
            viewModel.shouldASearchBePerformed = false
        }

        if (viewModel.lastVisibleListItem >= 10) applyViewVisibility(backToTopButton, View.VISIBLE)

        customSnackBar = CustomSnackBar.make(this.findViewById(android.R.id.content))

        backToTopButton.setOnClickListener {
            if (backToTopButton.visibility != View.GONE) applyViewVisibility(
                backToTopButton,
                View.INVISIBLE
            )
            hasBackToTopButtonBeenClicked = true
            scrollToTop(true)
        }

        githubProfileListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.hasAnyUserRequestedUpdatedData = true
            viewModel.hasUserTriggeredANewRequest = true
            swipeRefreshCall()
        }

        profileInfoRecyclerView.apply {
            setHasFixedSize(true)
            adapter = gistAdapter
            setupRecyclerViewAddOnScrollListener()
        }

        gistAdapter.setOnItemClicked(object : OnItemClicked {

            override fun onItemClick(adapterPosition: Int, id: Int, shouldBeDeleted: Boolean) {

                when (id) {
                    gistCell -> {
                        checkIfInternetConnectionIsAvailableCaller(
                            onConnectionAvailable = {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        FavoriteGistsActivity::class.java
                                    )
//                                        .putExtra(
//                                            Constants.gistId,
//                                            viewModel.provideGistId(
//                                                adapterPosition
//                                            )
//                                        )
                                )
                            },
                            onConnectionUnavailable = { showInternetConnectionStatusSnackBar(false) })
                    }

                    favorite -> {
                        if (shouldBeDeleted)
                            viewModel.deleteGist(adapterPosition)
                        else
                            viewModel.saveGist(adapterPosition)
                    }

                    retry -> paginationCall()
                }
            }
        })
    }

    private fun swipeRefreshCall() {
        callApiThroughViewModel { viewModel.requestUpdatedGithubProfiles() }
    }

    private fun paginationCall() {
        callApiThroughViewModel { viewModel.requestMoreGithubProfiles() }
    }

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {

        viewModel.mainListLiveData.observe(this) { githubUsersList ->

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            viewModel.shouldASearchBePerformed = false
            applyViewVisibility(githubProfileListSwipeRefreshLayout)

            shouldRecyclerViewAnimationBeExecuted =
                if (!viewModel.hasFirstSuccessfulCallBeenMade || viewModel.hasUserTriggeredANewRequest) {
                    gistAdapter.updateDataSource(githubUsersList)
                    true
                } else {
                    gistAdapter.updateDataSource(githubUsersList)
                    profileInfoRecyclerView.adapter?.notifyDataSetChanged()
                    applyViewVisibility(repositoryLoadingProgressBar, View.GONE)
                    false
                }

            if (viewModel.hasUserTriggeredANewRequest) viewModel.hasUserTriggeredANewRequest = false

            if (viewModel.hasAnyUserRequestedUpdatedData) {
                applyViewVisibility(githubProfileListSwipeRefreshLayout)
                gistAdapter.updateDataSource(githubUsersList)
                shouldRecyclerViewAnimationBeExecuted = true
                viewModel.hasAnyUserRequestedUpdatedData = false
            }

            if (shouldRecyclerViewAnimationBeExecuted)
                runLayoutAnimation(profileInfoRecyclerView)
            else
                shouldRecyclerViewAnimationBeExecuted = true
        }
    }

    private fun onError() {

        viewModel.errorSingleImmutableLiveDataEvent.observe(this) { error ->

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            if (viewModel.hasUserTriggeredANewRequest) viewModel.hasUserTriggeredANewRequest = false

            if (!shouldRecyclerViewAnimationBeExecuted)
                shouldRecyclerViewAnimationBeExecuted = true

            viewModel.shouldASearchBePerformed = true
            showErrorMessages(error)
        }
    }

    override fun setupExtras() {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {},
            onConnectionUnavailable = { showInternetConnectionStatusSnackBar(false) })
        setupInternetConnectionObserver()
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        with(recyclerView) {
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
            adapter?.notifyDataSetChanged()

            // For some reason, when a call that removes all previous items from the result list is made
            // (e.g: Pull-to-refresh or by clicking on the search icon), if one scrolled till some point of the list,
            // when the call is finished, the top of the list is not shown, which should happen. So a temporary solution
            // until i figure out what is going on is to go back to the top after any successful call that is required to
            // remove all previous list items so pagination call will not be affected by this
            scrollToTop(false)
            scheduleLayoutAnimation()
            applyViewVisibility(githubProfileListSwipeRefreshLayout)

            if (repositoryLoadingProgressBar.visibility == View.VISIBLE)
                applyViewVisibility(repositoryLoadingProgressBar, View.GONE)
        }
    }

    private inline fun callApiThroughViewModel(crossinline genericFunction: () -> Unit) {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {
                viewModel.shouldASearchBePerformed = false
                genericFunction.invoke()
                applyViewVisibility(repositoryLoadingProgressBar, View.VISIBLE)
                if (this::countingIdlingResource.isInitialized)
                    countingIdlingResource.increment()
            },
            onConnectionUnavailable = {
                showErrorMessages(R.string.no_connection_error)
            })
    }

    private fun showErrorMessages(message: Int) {
        applyViewVisibility(repositoryLoadingProgressBar, View.GONE)
        //SwipeRefreshLayout will only be visible if at least one successful call has been made so it will only be called if such condition is met
        if (viewModel.hasFirstSuccessfulCallBeenMade) applyViewVisibility(
            githubProfileListSwipeRefreshLayout
        )
        showSnackBar(
            this,
            getString(message),
            onDismissed = {
                checkIfInternetConnectionIsAvailableCaller(
                    {},
                    { showInternetConnectionStatusSnackBar(false) })
            })
    }

    private fun checkIfInternetConnectionIsAvailableCaller(
        onConnectionAvailable: () -> Unit,
        onConnectionUnavailable: () -> Unit
    ) {
        checkIfInternetConnectionIsAvailable(
            applicationContext,
            onConnectionAvailable,
            onConnectionUnavailable
        )
    }

    private fun setupInternetConnectionObserver() {
        internetConnectionAvailabilityObservable(applicationContext)
            .observe(this) { isInternetAvailable ->
                when (isInternetAvailable) {
                    true -> {
                        showInternetConnectionStatusSnackBar(true)
                        if (viewModel.hasFirstSuccessfulCallBeenMade) {
                            if (!viewModel.isThereAnOngoingCall && viewModel.isRetryListItemVisible && !viewModel.hasForbiddenErrorBeenEmitted) {
                                paginationCall()
                            }
                        } else
                            if (!viewModel.isThereAnOngoingCall)
                                callApiThroughViewModel { viewModel.requestUpdatedGithubProfiles() }
                    }
                    false -> showInternetConnectionStatusSnackBar(false)
                }
            }
    }

    private fun showInternetConnectionStatusSnackBar(isInternetConnectionAvailable: Boolean) {
        with(customSnackBar) {
            if (isInternetConnectionAvailable) {
                setText(getString(R.string.back_online_success_message)).setBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(
                        applicationContext,
                        R.color.green_700
                    )
                )
                if (isShown) dismiss()
            } else {
                setText(getString(R.string.no_connection_error)).setBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(
                        applicationContext,
                        R.color.red_700
                    )
                )
                show()
            }
        }
    }

    private fun setupRecyclerViewAddOnScrollListener() {
        profileInfoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val total = recyclerView.layoutManager?.itemCount
                val currentLastItem =
                    ((recyclerView.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()

                viewModel.lastVisibleListItem = currentLastItem

                if (currentLastItem in 4..9)
                    if (backToTopButton.visibility != View.GONE) {
                        applyViewVisibility(backToTopButton, View.GONE)
                        hasBackToTopButtonBeenClicked = false
                    }

                if (currentLastItem >= 10 && backToTopButton.visibility != View.VISIBLE && !hasBackToTopButtonBeenClicked) {
                    applyViewVisibility(
                        backToTopButton,
                        View.VISIBLE
                    )
                }

                if (currentLastItem == total?.minus(1) && !viewModel.isTheNumberOfItemsOfTheLastCallLessThanTwenty) {
                    /* This attribute was created to avoid  making an API call twice or more because sometimes this callback is called more than once, so,
                    the API call method won't be called until the previous API call finishes combining it with the 'isThereAnOngoingCall' attribute located in the
                    MainViewModel */
                    shouldRecyclerViewAnimationBeExecuted = false
                    if (!viewModel.isThereAnOngoingCall && !viewModel.isRetryListItemVisible) {
                        paginationCall()
                    }
                }
            }
        })
    }

    private fun scrollToTop(shouldScrollBeSmooth: Boolean) {
        if (shouldScrollBeSmooth)
            profileInfoRecyclerView.smoothScrollToPosition(0)
        else
            profileInfoRecyclerView.scrollToPosition(0)
    }

    fun bindIdlingResource(receivedCountingIdlingResource: CountingIdlingResource) {
        countingIdlingResource = receivedCountingIdlingResource
    }
}