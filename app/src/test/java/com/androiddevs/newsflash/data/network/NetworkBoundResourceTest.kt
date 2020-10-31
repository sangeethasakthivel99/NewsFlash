package com.androiddevs.newsflash.data.network

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class NetworkBoundResourceTest {

    @Nested
    inner class APISuccessShouldCache {

        val cacheCall = flowOf(Resource.success(TestDTO))

        val apiCall = Resource.success(TestResponse)

        @Mock
        lateinit var storeToDb: (suspend (TestDTO) -> Unit)

        @BeforeEach
        fun setup() {
            MockitoAnnotations.openMocks(this)
        }

        @Test
        fun `when api call is successful data should be cached`() = runBlockingTest {
            object : NetworkBoundResource<TestResponse, TestDTO>(
                cacheCall = {
                    cacheCall
                },

                apiCall = {
                    apiCall
                }
            ) {
                override val handleAPIResponse: Resource<TestResponse>.() -> TestDTO = {
                    TestDTO
                }

                override val saveToDb: (suspend (TestDTO) -> Unit)? = storeToDb

            }.get(true).collect()

            verify(storeToDb)
        }
    }

    @Nested
    inner class APIFailure {

        val cacheCall = flowOf(Resource.success(TestDTO))

        val apiCall = runBlocking {
            delay(500)
            Resource.getDefaultError<TestResponse>()
        }

        @Mock
        lateinit var storeToDb: (suspend (TestDTO) -> Unit)

        @BeforeEach
        fun setup() {
            MockitoAnnotations.openMocks(this)
        }

        @Test
        fun `when api call has failed, it should return error`() = runBlockingTest {
            object : NetworkBoundResource<TestResponse, TestDTO>(
                cacheCall = {
                    cacheCall
                },
                apiCall = { apiCall }
            ) {
                override val handleAPIResponse: Resource<TestResponse>.() -> TestDTO = { TestDTO }

                override val saveToDb: (suspend (TestDTO) -> Unit)? = storeToDb

            }.get(true).collect {
                println(it)
                assert(it.status == Status.ERROR)
                assert(!it.message.isNullOrEmpty())
            }

            verify(storeToDb, never())
        }
    }
}

object TestDTO
object TestResponse
