package veng.takehometest.rocket.repository.rocketstream

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit


@Configuration
class ExternalServiceConfiguration {
    @Value("\${external.service.advertisement.baseUrl}")
    lateinit var advertisementServiceUrl: String

    @Value("\${external.service.image.baseUrl}")
    lateinit var imageServiceUrl: String

    @Value("\${external.service.video.baseUrl}")
    lateinit var videoServiceUrl: String


    @Bean
    fun advertisementServiceAPI(): AdvertisementServiceAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(advertisementServiceUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(AdvertisementServiceAPI::class.java)
    }

    @Bean
    fun imageServiceAPI(): ImageServiceAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(imageServiceUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(ImageServiceAPI::class.java)
    }

    @Bean
    fun videoServiceAPI(): VideoServiceAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(videoServiceUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(VideoServiceAPI::class.java)
    }
}