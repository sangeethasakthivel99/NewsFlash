package com.androiddevs.newsflash.data.repository

import com.androiddevs.newsflash.data.local.db.dao.NewsDao
import com.androiddevs.newsflash.data.network.NetworkBoundResource
import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.contract.NewsAPILayer
import com.androiddevs.newsflash.data.network.models.News
import com.androiddevs.newsflash.data.network.models.RequestData
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.contract.NewsRepository
import com.androiddevs.newsflash.data.repository.mapper.toNewsArticle
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiLayer: NewsAPILayer,
    private val newsDao: NewsDao
) : NewsRepository {

    override suspend fun getBusinessNews(
        enableCaching: Boolean,
        topHeadlinesRequest: TopHeadlinesRequest
    ): Flow<List<NewsArticle>> {
        return object : NetworkBoundResource<News, List<NewsArticle>>(
            observableCacheCall = { newsDao.getAll() }) {

            override val apiCall: suspend () -> Resource<News> =
                { apiLayer.getBusinessNews(topHeadlinesRequest) }

            override val apiDataMap: News.() -> List<NewsArticle> = {
                articles.map {
                    it.toNewsArticle()
                }
            }

            override val saveToDb: (suspend (List<NewsArticle>) -> Unit)? = { data ->
                newsDao.insertAll(data)
            }

        }.get(RequestData(pageNumber = 1))
    }

}