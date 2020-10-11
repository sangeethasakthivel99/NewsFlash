package com.androiddevs.newsflash.ui.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.models.TopHeadlinesRequest
import com.androiddevs.newsflash.di.components.DaggerFakeAppComponent
import com.androiddevs.newsflash.ui.viewModel.fakes.FakeNewsRepositoryImpl
import com.androiddevs.newsflash.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import javax.inject.Inject

@ExperimentalCoroutinesApi
class HomeFragmentViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Inject
    lateinit var fakeRepo: FakeNewsRepositoryImpl

    @Inject
    lateinit var testDispatcher: DispatcherProvider

    private val homeFragmentViewModel: HomeFragmentViewModel by lazy {
        HomeFragmentViewModel(fakeRepo)
    }

    private val headlinesRequest = TopHeadlinesRequest("in")

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        DaggerFakeAppComponent.create().apply {
            inject(this@HomeFragmentViewModelTest)
        }
    }

    @Test
    fun `when page is created,default state should be loading`() = runBlockingTest {
        assert(homeFragmentViewModel.screenStates.value is HomeScreenStates.Loading)
    }

    @Test
    fun `when headlines response is success, new state should be pushed`() = runBlockingTest {
        assert(homeFragmentViewModel.screenStates.value is HomeScreenStates.Loading)
        homeFragmentViewModel.getTopHeadlines(headlinesRequest)
        assert(homeFragmentViewModel.screenStates.value is HomeScreenStates.TopHeadlinesReceived)
    }

    @Test
    fun `when headlines response is unsuccessful, error state should be pushed`() =
        runBlockingTest {
            fakeRepo.apiStatus = Status.ERROR
            assert(homeFragmentViewModel.screenStates.value is HomeScreenStates.Loading)
            homeFragmentViewModel.getTopHeadlines(headlinesRequest)
            assert(homeFragmentViewModel.screenStates.value is HomeScreenStates.ErrorState)
        }
}