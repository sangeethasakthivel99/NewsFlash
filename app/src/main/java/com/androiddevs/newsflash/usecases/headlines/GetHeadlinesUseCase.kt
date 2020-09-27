package com.androiddevs.newsflash.usecases.headlines

//class GetHeadlinesUseCase @Inject constructor(
//    private val newsRepository: NewsRepository
//) {
//
//    operator fun invoke(headlinesRequest: TopHeadlinesRequest): Flow<HomeScreenStates> {
//        return flow {
//            val response = newsRepository.getBusinessNews(headlinesRequest)
//            if (response.status == Status.SUCCESS) {
//                response.data?.let {
//                    HomeScreenStates.TopHeadlinesReceived(it)
//                } ?: kotlin.run {
//                  HomeScreenStates.ErrorState
//                }
//            } else {
//                HomeScreenStates.ErrorState
//            }
//        }
//    }
//}