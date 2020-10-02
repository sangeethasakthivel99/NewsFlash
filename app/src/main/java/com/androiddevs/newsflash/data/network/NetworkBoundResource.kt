package com.androiddevs.newsflash.data.network

import com.androiddevs.newsflash.data.network.apiwrapper.Resource
import com.androiddevs.newsflash.data.network.apiwrapper.Status
import com.androiddevs.newsflash.data.network.models.RequestData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<ApiType, ReturnType>(
    val cacheCall: (suspend () -> ReturnType)? = null,
    val observableCacheCall: (suspend () -> Flow<ReturnType>)? = null
) {

    abstract val apiCall: (suspend () -> Resource<ApiType>)

    fun get(requestData: RequestData) = flow<ReturnType> {

    }

    suspend fun makeApiCall(requestData: RequestData): Flow<Resource<ReturnType>> = flow<Resource<ReturnType>> {
        val response = apiCall()
        if (response.status == Status.SUCCESS) {
            response.data?.let {
                val domainData = it.apiDataMap()

                if (requestData.shouldCache)
                    saveToDb?.invoke(domainData)

                emit (Resource.success(domainData))
            } ?: run {
                emit (Resource.getDefaultError())
            }
        } else {
            emit (Resource.error(response.message, response.errorException))
        }
    }

    open val saveToDb: (suspend (ReturnType) -> Unit)? = null

    abstract val apiDataMap: (ApiType.() -> ReturnType)



}