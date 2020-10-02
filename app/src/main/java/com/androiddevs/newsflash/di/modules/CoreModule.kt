package com.androiddevs.newsflash.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.androiddevs.newsflash.data.local.db.NewsDatabase
import com.androiddevs.newsflash.data.local.db.dao.NewsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [APIBinder::class])
class CoreModule {

    companion object {
        const val PREF_NAME = "com.androiddevs.newsflash"
        const val BASE_URL = "com.androiddevs.newsflash"
    }

    @Singleton
    @Provides
    fun providesSharedPreferences(context: Application): SharedPreferences =
        context.applicationContext.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    @Singleton
    @Provides
    fun providesNewsDatabase(context: Application): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, "news_db")
            .build()


    @Provides
    fun providesNewsDao(newsDatabase: NewsDatabase): NewsDao =
        newsDatabase.getNewsDao()

}