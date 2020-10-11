package com.androiddevs.newsflash.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.repository.models.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newsList: List<NewsArticle>)


    @Query("Select * FROM news_details")
    fun getAllFlow(): Flow<List<NewsArticle>>

    fun getAllArticlesFlow() =
        getAllFlow().map {
            Resource.success(it)
        }

    suspend fun getAllArticles(): List<NewsArticle> {
        val allRecords = getAll()
        return if (!allRecords.isNullOrEmpty()) {
            allRecords
        } else {
            listOf()
        }
    }

    @Query("Select * FROM news_details")
    suspend fun getAll(): List<NewsArticle>
}