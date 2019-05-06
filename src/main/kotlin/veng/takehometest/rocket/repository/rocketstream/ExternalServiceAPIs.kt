package veng.takehometest.rocket.repository.rocketstream

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface  AdvertisementServiceAPI {
    @GET("advertisements")
    fun getAdvertisementsOfContainer(@Query("containerId") containerId: Int): Call<AdvertisementsWrapper>

    @GET("advertisements")
    fun getAdvertisements(): Call<AdvertisementsWrapper>
}

interface ImageServiceAPI {
    @GET("images")
    fun getImagesOfContainer(@Query("containerId") containerId: Int): Call<ImagesWrapper>

    @GET("images")
    fun getImages(): Call<ImagesWrapper>
}

interface VideoServiceAPI {
    @GET("videos")
    fun getVideosOfContainer(@Query("containerId") containerId: Int): Call<VideosWrapper>

    @GET("videos")
    fun getVideos(): Call<VideosWrapper>

    @GET("{videoId}")
    fun getVideo(@Path("videoId") videoId: Int): Call<RocketStreamVideo>

    @GET("{videoId}/asset-references")
    fun getVideoAssets(@Path("videoId") videoId: Int, @Query("assetType") assetType: String): Call<VideoAssetsWrapper>
}