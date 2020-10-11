package com.androiddevs.newsflash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.models.RequestData
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.contract.NewsRepository
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeFragmentViewModel constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val screenStates: MutableStateFlow<HomeScreenStates> =
        MutableStateFlow(HomeScreenStates.Loading)

    val events: MutableStateFlow<HomeScreenEvents?> = MutableStateFlow(null)

    private var pageNumber = 1

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
        newsRepository.getNewsArticles(RequestData(true, pageNumber), headlinesRequest)
            .onEach { resource ->
                val state = if (resource.status == Status.LOADING) {
                    HomeScreenStates.Loading
                } else {
                    val articlesList = resource.data ?: listOf()
                    if (!articlesList.isNullOrEmpty()) {
                        HomeScreenStates.TopHeadlinesReceived(articlesList)
                    } else {
                        HomeScreenStates.ErrorState
                    }
                }
                screenStates.value = state
            }.launchIn(viewModelScope)
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