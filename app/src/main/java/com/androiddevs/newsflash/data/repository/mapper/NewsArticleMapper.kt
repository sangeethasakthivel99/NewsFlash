package com.androiddevs.newsflash.data.repository.mapper

import com.androiddevs.newsflash.data.network.models.News
import com.androiddevs.newsflash.data.repository.models.NewsArticle

fun News.Article.toNewsArticle(): NewsArticle {
    return NewsArticle(
        sourceName = source?.name ?: "",
        authorName = author,
        articleTitle = title,
        description = description,
        articleUrl = url,
        imageUrl = urlToImage,
        publishedDate = publishedAt ?: ""
    )
}