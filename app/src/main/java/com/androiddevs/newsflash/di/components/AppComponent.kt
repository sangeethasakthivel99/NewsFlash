package com.androiddevs.newsflash.di.components

import android.app.Application
import com.androiddevs.newsflash.di.modules.CoreBinder
import com.androiddevs.newsflash.di.modules.CoreModule
import com.androiddevs.newsflash.ui.MainActivity
import com.androiddevs.newsflash.ui.fragments.HomeFragment
import com.androiddevs.newsflash.ui.fragments.LoginFragment
import com.androiddevs.newsflash.ui.fragments.SplashFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [CoreBinder::class, CoreModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(loginFragment: LoginFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(splashFragment: SplashFragment)
    fun inject(mainActivity: MainActivity)
}