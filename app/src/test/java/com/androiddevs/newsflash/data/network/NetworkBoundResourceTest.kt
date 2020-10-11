package com.androiddevs.newsflash.data.network

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class NetworkBoundResourceTest {

    val cacheCall = flowOf(Resource.success(TestDTO))

    val apiCall = Resource.success(TestResponse)

    @Mock
    lateinit var storeToDb: ((TestDTO) -> Unit)

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
            }) {
            override val handleAPIResponse: Resource<TestResponse>.() -> TestDTO = {
                TestDTO
            }

            override val saveToDb: (suspend (TestDTO) -> Unit)? = {

                storeToDb(it)
            }

        }.get(true).let {
            verify(storeToDb)
        }
    }
}

object TestDTO
object TestResponse
