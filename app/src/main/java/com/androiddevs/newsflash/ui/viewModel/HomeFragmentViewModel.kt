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

    }

    fun getTopHeadlines(headlinesRequest: TopHeadlinesRequest) {
        viewModelScope.launch(appDispatchers.ioDispatcher) {
            val response = newsRepository.getBusinessNews(headlinesRequest)
            if (response.status == Status.SUCCESS) {
                response.data?.let {
                    screenStates.value = HomeScreenStates.TopHeadlinesReceived(it)
                } ?: kotlin.run {
                    screenStates.value = HomeScreenStates.ErrorState
                }
            } else {
                screenStates.value = HomeScreenStates.ErrorState
            }
        }
    }

}

sealed class HomeScreenStates {
    object Loading : HomeScreenStates()
    object ErrorState : HomeScreenStates()
    data class TopHeadlinesReceived(val articleList: List<NewsArticle>) : HomeScreenStates()
}


sealed class HomeScreenEvents {

}