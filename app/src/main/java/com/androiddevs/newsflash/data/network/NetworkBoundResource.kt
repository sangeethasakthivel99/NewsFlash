package com.androiddevs.newsflash.data.network

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ApiType, ReturnType>(
    private val cacheCall: (suspend () -> Flow<Resource<ReturnType>>),
    private val apiCall: (suspend () -> Resource<ApiType>)
) {

    suspend fun get(shouldCache: Boolean): Flow<Resource<ReturnType>> {
        return if (shouldCache) {
            flowOf(
                makeApiCall(shouldCache),
                cacheCall()
            ).flatMapMerge { it }
        } else {
            makeApiCall(shouldCache)
        }
    }

    private suspend fun makeApiCall(shouldCache: Boolean) = flow {
        val response = apiCall()
        if (response.status == Status.SUCCESS) {
            val domainData = response.handleAPIResponse()
            val paginatedData = paginate(domainData)
            if (domainData != null && shouldCache) //1st run directly insert
                saveToDb?.invoke(paginatedData)
            emit(Resource.success(paginatedData))
        } else {
            emit(Resource.error<ReturnType>(response.message, response.errorException))
        }
    }

    open val saveToDb: (suspend (ReturnType) -> Unit)? = null

    open val paginate: (suspend (ReturnType) -> ReturnType) = { domainData -> domainData }

    abstract val handleAPIResponse: (Resource<ApiType>.() -> ReturnType)


}