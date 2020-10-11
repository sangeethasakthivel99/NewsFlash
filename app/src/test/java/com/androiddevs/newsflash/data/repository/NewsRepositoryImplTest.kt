package com.androiddevs.newsflash.data.repository

import com.androiddevs.newsflash.data.local.db.dao.NewsDao
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.models.RequestData
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.data.repository.fakes.FakeNewsAPILayerImpl
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import com.androiddevs.newsflash.di.components.DaggerFakeAppComponent
import com.androiddevs.newsflash.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NewsRepositoryImplTest {

    @Inject
    lateinit var fakeAPILayerFake: FakeNewsAPILayerImpl

    @Inject
    lateinit var testDispatcher: DispatcherProvider

    @Mock
    lateinit var newsDao: NewsDao

    private val repository by lazy { NewsRepositoryImpl(fakeAPILayerFake, newsDao, testDispatcher) }

    private val headlinesRequest = TopHeadlinesRequest("in")

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        DaggerFakeAppComponent.create().apply {
            inject(this@NewsRepositoryImplTest)
        }
    }

    @Nested
    inner class NonCachedApiCallTest {

        private val requestData = RequestData(false, 1)

        @Test
        fun `when the api is unsuccessful, repository should return proper error message`() =
            runBlockingTest {
                fakeAPILayerFake.apiStatus = Status.ERROR
                val response = repository.getNewsArticles(requestData, headlinesRequest)
                response.collect {
                    assert(it.message!!.isNotEmpty())
                }
            }

        @Test
        fun `when a successful request is made, repository should return list of News Articles`() =
            runBlockingTest {
                fakeAPILayerFake.apiStatus = Status.SUCCESS
                val response = repository.getNewsArticles(requestData, headlinesRequest)
                response.collect {
                    assert(it.data is List<NewsArticle>)
                }
            }
    }

    @Nested
    inner class CachedApiCallTest {

        private val requestData = RequestData(true, 1)

        @Test
        fun `when the api is successful, data should be cached`() =
            runBlockingTest {
                fakeAPILayerFake.apiStatus = Status.ERROR
                val response = repository.getNewsArticles(requestData, headlinesRequest)
                response.collect {
                    Mockito.verify(it.data?.let { it1 -> newsDao.insertAll(it1) })
                }
            }
    }
}

