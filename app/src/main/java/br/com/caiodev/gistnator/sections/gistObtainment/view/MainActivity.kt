package br.com.caiodev.gistnator.sections.gistObtainment.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import br.com.caiodev.gistnator.R
import br.com.caiodev.gistnator.sections.favoriteGists.view.FavoriteGistsActivity
import br.com.caiodev.gistnator.sections.gistDetails.view.GistDetailsActivity
import br.com.caiodev.gistnator.sections.gistObtainment.view.adapter.GistAdapter
import br.com.caiodev.gistnator.sections.gistObtainment.viewModel.MainViewModel
import br.com.caiodev.gistnator.sections.gistObtainment.viewModel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.heart_component.view.*
import utils.base.flow.ViewFlow
import utils.constants.Constants
import utils.constants.Constants.favorite
import utils.constants.Constants.gistCell
import utils.constants.Constants.itemDeleted
import utils.constants.Constants.itemInserted
import utils.constants.Constants.retry
import utils.extensions.*
import utils.interfaces.OnItemClicked
import utils.interfaces.viewTypes.ViewType
import utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import utils.snackBar.CustomSnackBar

class MainActivity :
    AppCompatActivity(R.layout.activity_main),
    ViewFlow {

    private var hasBackToTopButtonBeenClicked = false
    private lateinit var countingIdlingResource: CountingIdlingResource

    private lateinit var customSnackBar: CustomSnackBar

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)
    }

    private val gistAdapter by lazy {
        GistAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        handleViewModel()
        setupExtras()
        performCall()
    }

    private fun performCall() {
        if (!viewModel.hasFirstSuccessfulCallBeenMade)
            callApiThroughViewModel { viewModel.requestUpdatedGithubGists() }
    }

    override fun setupView() {

        setSupportActionBar(mainToolbar)

        //Condition when users rotate the screen and the activity gets destroyed
        if (viewModel.isThereAnOngoingCall) {
            applyViewVisibility(repositoryLoadingProgressBar, View.VISIBLE)
            viewModel.shouldASearchBePerformed = false
        }

        if (viewModel.lastVisibleListItem >= 10) applyViewVisibility(backToTopButton, View.VISIBLE)

        changeDrawable(saveFavoriteComponent.favoriteImageView, R.drawable.ic_saved_favorite)

        goToFavoritesActivityLinearLayout.setOnClickListener {
            startActivity(Intent(applicationContext, FavoriteGistsActivity::class.java))
        }

        customSnackBar = CustomSnackBar.make(this.findViewById(android.R.id.content))

        backToTopButton.setOnClickListener {
            if (backToTopButton.visibility != View.GONE) applyViewVisibility(
                backToTopButton,
                View.INVISIBLE
            )
            hasBackToTopButtonBeenClicked = true
            scrollToTop(true)
        }

        gistRecyclerView.apply {
            setHasFixedSize(true)
            adapter = gistAdapter
            setupRecyclerViewAddOnScrollListener()
        }

        gistAdapter.setOnItemClicked(object : OnItemClicked {

            override fun onItemClick(
                adapterPosition: Int,
                id: Int,
                shouldBeDeleted: Boolean,
                itemImage: Bitmap?
            ) {

                when (id) {
                    gistCell -> {
                        checkIfInternetConnectionIsAvailableCaller(
                            onConnectionAvailable = {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        GistDetailsActivity::class.java
                                    ).putExtra(
                                        Constants.gistId,
                                        viewModel.provideGistId(
                                            adapterPosition
                                        )
                                    )
                                )
                            },
                            onConnectionUnavailable = {
                                showInternetConnectionStatusSnackBar(
                                    applicationContext,
                                    customSnackBar,
                                    false
                                )
                            })
                    }

                    favorite -> {
                        if (shouldBeDeleted)
                            viewModel.deleteGist(adapterPosition)
                        else
                            itemImage?.let {
                                viewModel.saveGist(adapterPosition, it)
                            }
                    }

                    retry -> paginationCall()
                }
            }
        })
    }

    private fun paginationCall() {
        callApiThroughViewModel { viewModel.requestMoreGithubGists() }
    }

    override fun handleViewModel() {
        onSuccess()
        onError()
    }

    private fun onSuccess() {

        viewModel.liveData.observe(this) { value ->

            when (value) {

                is List<*> -> {

                    with(viewModel.castAttributeThroughViewModel<List<ViewType>>(value)) {

                        if (this@MainActivity::countingIdlingResource.isInitialized)
                            countingIdlingResource.decrement()

                        viewModel.shouldASearchBePerformed = false

                        if (!viewModel.hasFirstSuccessfulCallBeenMade || viewModel.hasUserTriggeredANewRequest) {
                            gistAdapter.updateDataSource(this)
                            runLayoutAnimation(gistRecyclerView)
                        } else {
                            gistAdapter.updateDataSource(this)
                            gistRecyclerView.adapter?.notifyDataSetChanged()
                            applyViewVisibility(repositoryLoadingProgressBar, View.GONE)
                        }

                        if (viewModel.hasUserTriggeredANewRequest) viewModel.hasUserTriggeredANewRequest =
                            false

                        if (viewModel.hasAnyUserRequestedUpdatedData) {
                            gistAdapter.updateDataSource(this)
                            viewModel.hasAnyUserRequestedUpdatedData = false
                        }
                    }
                }

                is Int -> {
                    when (value) {
                        itemInserted ->
                            showSnackBar(this, "Gist saved") {}

                        itemDeleted ->
                            showSnackBar(this, "Gist deleted") {}
                    }
                }
            }
        }
    }

    private fun onError() {

        viewModel.errorSingleImmutableLiveDataEvent.observe(this) { error ->

            if (this::countingIdlingResource.isInitialized)
                countingIdlingResource.decrement()

            if (viewModel.hasUserTriggeredANewRequest) viewModel.hasUserTriggeredANewRequest = false

            viewModel.shouldASearchBePerformed = true
            showErrorMessages(error)
        }
    }

    override fun setupExtras() {
        checkIfInternetConnectionIsAvailableCaller(
            onConnectionAvailable = {},
            onConnectionUnavailable = {
                showInternetConnectionStatusSnackBar(
                    applicationContext,
                    customSnackBar,
                    false
                )
            })
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

            if (viewModel.hasFirstSuccessfulCallBeenMade) {
                if (repositoryLoadingProgressBar.visibility == View.VISIBLE)
                    applyViewVisibility(repositoryLoadingProgressBar, View.GONE)
            }
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
        showSnackBar(
            this,
            getString(message),
            onDismissed = {
                checkIfInternetConnectionIsAvailableCaller(
                    {},
                    {
                        showInternetConnectionStatusSnackBar(
                            applicationContext,
                            customSnackBar,
                            false
                        )
                    })
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
                        showInternetConnectionStatusSnackBar(
                            applicationContext,
                            customSnackBar,
                            true
                        )
                        if (viewModel.hasFirstSuccessfulCallBeenMade) {
                            if (!viewModel.isThereAnOngoingCall && viewModel.isRetryListItemVisible && !viewModel.hasForbiddenErrorBeenEmitted) {
                                paginationCall()
                            }
                        } else
                            if (!viewModel.isThereAnOngoingCall)
                                callApiThroughViewModel { viewModel.requestUpdatedGithubGists() }
                    }
                    else -> showInternetConnectionStatusSnackBar(
                        applicationContext,
                        customSnackBar,
                        false
                    )
                }
            }
    }

    private fun setupRecyclerViewAddOnScrollListener() {
        gistRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    if (!viewModel.isThereAnOngoingCall && !viewModel.isRetryListItemVisible) {
                        paginationCall()
                    }
                }
            }
        })
    }

    private fun scrollToTop(shouldScrollBeSmooth: Boolean) {
        if (shouldScrollBeSmooth)
            gistRecyclerView.smoothScrollToPosition(0)
        else
            gistRecyclerView.scrollToPosition(0)
    }

    fun bindIdlingResource(receivedCountingIdlingResource: CountingIdlingResource) {
        countingIdlingResource = receivedCountingIdlingResource
    }
}