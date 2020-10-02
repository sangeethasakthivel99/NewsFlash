package com.androiddevs.newsflash.data.repository

import com.androiddevs.newsflash.data.local.db.dao.NewsDao
import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.contract.NewsAPILayer
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.contract.NewsRepository
import com.androiddevs.newsflash.data.repository.mapper.toNewsArticle
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiLayer: NewsAPILayer,
    private val newsDao: NewsDao
) : NewsRepository {

    override suspend fun getBusinessNews(
        enableCaching: Boolean,
        topHeadlinesRequest: TopHeadlinesRequest
    ): Flow<Resource<List<NewsArticle>>> {
        return if (enableCaching) {
            getHeadlines(enableCaching, topHeadlinesRequest)
            newsDao.getAll().map { Resource.success(it) }
        } else {
            flow { getHeadlines(enableCaching, topHeadlinesRequest) }
        }
    }

    private suspend fun getHeadlines(
        enableCaching: Boolean,
        topHeadlinesRequest: TopHeadlinesRequest
    ): Resource<List<NewsArticle>> {
        val response = apiLayer.getBusinessNews(topHeadlinesRequest)
        if (response.status == Status.SUCCESS) {
            response.data?.let {
                val articles = it.articles.map { it.toNewsArticle() }

                if (enableCaching)
                    newsDao.insertAll(articles)

                return (Resource.success(articles))
            } ?: run {
                return (Resource.getDefaultError())
            }
        } else {
            return (Resource.error(response.message, response.errorException))
        }
    }

}