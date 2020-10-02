package com.androiddevs.newsflash.data.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_details")
data class NewsArticle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceName: String,
    val authorName: String,
    val articleTitle: String,
    val description: String,
    val articleUrl: String,
    val imageUrl: String,
    val publishedDate: String
)

