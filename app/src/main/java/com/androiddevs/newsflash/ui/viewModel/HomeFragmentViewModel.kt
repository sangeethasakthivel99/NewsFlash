package com.androiddevs.newsflash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.contract.NewsRepository
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import com.androiddevs.newsflash.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeFragmentViewModel constructor(
    private val newsRepository: NewsRepository,
    private val appDispatchers: DispatcherProvider
) : ViewModel() {

    val screenStates: MutableStateFlow<HomeScreenStates> =
        MutableStateFlow(HomeScreenStates.Loading)

    val events: MutableStateFlow<HomeScreenEvents?> = MutableStateFlow(null)

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is HomeScreenEvents.GetHeadLines -> {
                        getTopHeadlines(event.topHeadlinesRequest)
                    }
                }
            }
        }
    }

    suspend fun getTopHeadlines(headlinesRequest: TopHeadlinesRequest) {
        viewModelScope.launch(appDispatchers.ioDispatcher) {
            val response = newsRepository.getBusinessNews(headlinesRequest)
            val state = if (response.status == Status.SUCCESS) {
                response.data?.let {
                    HomeScreenStates.TopHeadlinesReceived(it)
                } ?: kotlin.run {
                    HomeScreenStates.ErrorState
                }
            } else {
                HomeScreenStates.ErrorState
            }
            screenStates.value = state
        }
    }

}

sealed class HomeScreenStates {
    object Loading : HomeScreenStates()
    object ErrorState : HomeScreenStates()
    data class TopHeadlinesReceived(val articleList: List<NewsArticle>) : HomeScreenStates()
}


sealed class HomeScreenEvents {
    data class GetHeadLines(val topHeadlinesRequest: TopHeadlinesRequest) : HomeScreenEvents()
}