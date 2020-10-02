package com.androiddevs.newsflash.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androiddevs.newsflash.data.local.db.dao.NewsDao
import com.androiddevs.newsflash.data.repository.models.NewsArticle

@Database(entities = [NewsArticle::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun getNewsDao(): NewsDao
}