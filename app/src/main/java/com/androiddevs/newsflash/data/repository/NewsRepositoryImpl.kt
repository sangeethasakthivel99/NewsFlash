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
import com.androiddevs.newsflash.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiLayer: NewsAPILayer,
    private val newsDao: NewsDao,
    private val appDispatchers: DispatcherProvider
) : NewsRepository {

    override suspend fun getNewsArticles(
        requestData: RequestData,
        topHeadlinesRequest: TopHeadlinesRequest
    ): Flow<Resource<List<NewsArticle>>> {
        return object : NetworkBoundResource<News, List<NewsArticle>>(
            cacheCall = {
                newsDao.getAllFlow()
            },
            apiCall = {
                apiLayer.getBusinessNews(topHeadlinesRequest)
            }
        ) {
            override val handleAPIResponse: Resource<News>.() -> List<NewsArticle> = {
                if (status == Status.SUCCESS) {
                    data?.let {
                        it.articles.map {
                            it.toNewsArticle()
                        }
                    } ?: listOf()
                } else {
                    listOf()
                }
            }

            override val saveToDb: (suspend (List<NewsArticle>) -> Unit)? = { data ->
                newsDao.insertAll(data)
            }

            override val paginate: (suspend (List<NewsArticle>) -> List<NewsArticle>) =
                { domainData ->
                    if (requestData.pageNumber > 1) {
                        if (domainData.isNullOrEmpty()) {
                            newsDao.getAllArticles()
                        } else {
                            newsDao.getAllArticles().toMutableList() + domainData
                        }
                    } else {
                        domainData
                    }
                }

        }.get(requestData.shouldCache).map {
            if (it.isNullOrEmpty()) {
                Resource.loading()
            } else {
                Resource.success(it)
            }
        }.flowOn(appDispatchers.ioDispatcher)
    }

}