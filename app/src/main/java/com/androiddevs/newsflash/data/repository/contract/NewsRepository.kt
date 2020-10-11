package com.androiddevs.newsflash.data.repository.contract

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.models.RequestData
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getNewsArticles(
        requestData: RequestData,
        topHeadlinesRequest: TopHeadlinesRequest
    ): Flow<Resource<List<NewsArticle>>>
}