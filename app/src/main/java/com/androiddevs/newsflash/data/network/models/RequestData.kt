package com.androiddevs.newsflash.data.network.models

data class RequestData(
    val shouldCache: Boolean = true,
    val pageNumber: Int,
    val pageSize: Int = 10
)