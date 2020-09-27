package com.androiddevs.newsflash.di.components

import com.androiddevs.newsflash.data.repository.NewsRepositoryImplTest
import com.androiddevs.newsflash.di.modules.FakeAppBinders
import com.androiddevs.newsflash.ui.viewModel.HomeFragmentViewModelTest
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(modules = [FakeAppBinders::class])
interface FakeAppComponent {
    fun inject(homeFragmentViewModelTest: HomeFragmentViewModelTest)
    fun inject(homeFragmentViewModelTest: NewsRepositoryImplTest)
}