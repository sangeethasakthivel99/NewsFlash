package com.androiddevs.newsflash.ui.viewModel.fakes

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.models.RequestData
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.contract.NewsRepository
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import com.androiddevs.newsflash.utils.TestableAPIStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeNewsRepositoryImpl @Inject constructor() : NewsRepository, TestableAPIStatus {

    override var apiStatus = Status.SUCCESS


    private fun getSuccessResponse(): Resource<List<NewsArticle>> {
        return Resource.success(listOf())
    }

    private fun getErrorResponse(): Resource<List<NewsArticle>> {
        return Resource.error("", null)
    }

    override suspend fun getNewsArticles(
        requestData: RequestData,
        topHeadlinesRequest: TopHeadlinesRequest
    ): Flow<Resource<List<NewsArticle>>> {
        return flow {
            if (apiStatus == Status.SUCCESS) {
                emit(getSuccessResponse())
            } else {
                emit(getErrorResponse())
            }
        }
    }

}