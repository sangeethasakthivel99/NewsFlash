package com.androiddevs.newsflash.data.network

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<ApiType, ReturnType>(
    private val cacheCall: (suspend () -> Flow<ReturnType?>),
    private val apiCall: (suspend () -> Resource<ApiType>)
) {

    suspend fun get(shouldCache: Boolean) = combine(
        makeApiCall(shouldCache),
        cacheCall()
    ) { api, cached ->
        if (shouldCache) {
            cached ?: api
        } else
            api
    }


    private suspend fun makeApiCall(shouldCache: Boolean) = flow {
        val response = apiCall()
        val domainData = response.handleAPIResponse()
        val paginatedData = paginate(domainData)
        if (domainData != null && shouldCache) //1st run directly insert
            saveToDb?.invoke(paginatedData)
        emit(paginatedData)
    }

    open val saveToDb: (suspend (ReturnType) -> Unit)? = null

    open val paginate: (suspend (ReturnType) -> ReturnType) = { domainData -> domainData }

    abstract val handleAPIResponse: (Resource<ApiType>.() -> ReturnType)


}