package com.androiddevs.newsflash.data.repository.contract

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getBusinessNews(
        enableCaching: Boolean,
        topHeadlinesRequest: TopHeadlinesRequest
    ): Flow<List<NewsArticle>>
}