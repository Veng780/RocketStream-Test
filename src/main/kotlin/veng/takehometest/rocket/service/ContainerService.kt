package veng.takehometest.rocket.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Response
import veng.takehometest.rocket.model.*
import veng.takehometest.rocket.repository.rocketstream.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.streams.toList

interface ContainerService {
    fun getContainers(): Collection<Container>
    fun getContainer(containerId: Int): Container

    fun getAdvertisements(containerId: Int): Collection<Advertisement>
    fun getImages(containerId: Int): Collection<Image>
    fun getVideos(containerId: Int): Collection<Video>
}

@Service
class ContainerServiceImpl : ContainerService {
    @Autowired
    lateinit var advertisementServiceAPI: AdvertisementServiceAPI

    @Autowired
    lateinit var imageServiceAPI: ImageServiceAPI

    @Autowired
    lateinit var videoServiceAPI: VideoServiceAPI


    override fun getContainers(): Collection<Container> {
        val executorService = Executors.newFixedThreadPool(3)

        val vidsCall = Callable<Map<Int, MutableList<Video>>> {
            val result = mutableMapOf<Int, MutableList<Video>>()

            val respVideo = videoServiceAPI.getVideos().execute()
            if (respVideo.isSuccessful) {
                respVideo.body()!!.videos.filter { it.containerId!=null }.forEach {
                    val videos = result.getOrDefault(it.containerId, mutableListOf())
                    videos.add(buildVideoInfo(it))
                    result[it.containerId!!] = videos
                }
            } else {
                logUnsuccessResponse("containers videos problem", respVideo)
            }

            logger.debug("finish videos call for containers ")
            result
        }

        val imagesCall = Callable<Map<Int, MutableList<Image>>> {
            val result = mutableMapOf<Int, MutableList<Image>>()
            val response = imageServiceAPI.getImages().execute()
            if (response.isSuccessful) {
                response.body()!!.images.filter { it.containerId!=null }.forEach {
                    val images = result.getOrDefault(it.id, mutableListOf())
                    images.add(Image(id = it.id, name = it.name, url = it.url))
                    result[it.containerId!!] = images
                }
            } else {
                logUnsuccessResponse("containers images problem", response)
            }
            result
        }

        val adsCall = Callable<Map<Int, MutableList<Advertisement>>> {
            val result = mutableMapOf<Int, MutableList<Advertisement>>()
            val response = advertisementServiceAPI.getAdvertisements().execute()
            if (response.isSuccessful) {
                response.body()!!.advertisements.filter { it.containerId!=null }.forEach {
                    val ads = result.getOrDefault(it.id, mutableListOf())
                    ads.add(Advertisement(id = it.id, name = it.name, url = it.url))
                    result[it.containerId!!] = ads
                }
            } else {
                logUnsuccessResponse("containers ads problem", response)
            }
            result
        }

        val containersMap: Map<Int, Container>
        try {
            val vidsFuture = executorService.submit(vidsCall)
            val imagesFuture = executorService.submit(imagesCall)
            val adsFuture = executorService.submit(adsCall)

            val imagesMap = imagesFuture.get()
            val adsMap = adsFuture.get()
            val videosMap =  vidsFuture.get()

            containersMap = videosMap.map {
                val (containerId, videos) = it
                Pair(containerId, Container(id = containerId, videos = videos))
            }.toMap()

            imagesMap.forEach {
                val (containterId, images) = it
                if (containersMap.containsKey(containterId)) {
                    containersMap[containterId]!!.images = images
                }
            }

            adsMap.forEach {
                val (containterId, ads) = it
                if (containersMap.containsKey(containterId)) {
                    containersMap[containterId]!!.ads = ads
                }
            }

        } finally {
            executorService.shutdown()
        }

        return containersMap.values
    }

    override fun getContainer(containerId: Int): Container {
        val videos = getVideos(containerId)
        val ads = getAdvertisements(containerId)
        val images = getImages(containerId)

        return Container(id = containerId, ads = ads, images = images, videos = videos)
    }

    override fun getAdvertisements(containerId: Int): Collection<Advertisement> {
        val response =  advertisementServiceAPI.getAdvertisementsOfContainer(containerId!!).execute()
        if (! response.isSuccessful) {
            logUnsuccessResponse("advertisement container $containerId", response)
            return emptyList()
        }

        logger.debug("advertisements $containerId  OK")
        return when {
            response.body()!!.advertisements==null -> emptyList()
            else -> response.body()!!.advertisements.map {
                Advertisement(id = it.id, name = it.name, url = it.url)
            }
        }
    }

    override fun getImages(containerId: Int): Collection<Image> {
        val response =  imageServiceAPI.getImagesOfContainer(containerId!!).execute()
        if (! response.isSuccessful) {
            logUnsuccessResponse("images container $containerId", response)
            return emptyList()
        }

        logger.debug("images $containerId  OK")
        return when {
            response.body()!!.images ==null -> emptyList()
            else -> response.body()!!.images.map {
                Image(id = it.id, name = it.name, url = it.url)
            }
        }
    }

    override fun getVideos(containerId: Int): Collection<Video> {
        val respVideo = videoServiceAPI.getVideosOfContainer(containerId!!).execute()
        if (! respVideo.isSuccessful) {
            logUnsuccessResponse("videos container $containerId", respVideo)
            return emptyList()
        }

        logger.debug("videos container $containerId  OK")
        return when {
            respVideo.body()!!.videos==null -> emptyList()
            else -> respVideo.body()!!.videos.map {
                buildVideoInfo(it)
            }
        }
    }

    private fun buildVideoInfo(rocketStreamVideo: RocketStreamVideo): Video {
        var type: TypeOfVideo? = rocketStreamVideo.type?.run { TypeOfVideo.valueOf(this) }

        val assets = mutableListOf<Asset>()
        var respAssetType = videoServiceAPI.getVideoAssets(rocketStreamVideo.id, RocketStreamAssetType.ad.name).execute()
        if (respAssetType.isSuccessful) {
            respAssetType.body()!!.assetTypes.forEach {
                assets.add(Asset(assetId = it.assetId, assertType = AsserType.AD))
            }
        } else {
            logUnsuccessResponse("video ad asset ${rocketStreamVideo.id}", respAssetType)
        }

        respAssetType = videoServiceAPI.getVideoAssets(rocketStreamVideo.id, RocketStreamAssetType.image.name).execute()
        if (respAssetType.isSuccessful) {
            respAssetType.body()!!.assetTypes.forEach {
                assets.add(Asset(assetId = it.assetId, assertType = AsserType.IMAGE))
            }
        } else {
            logUnsuccessResponse("video image asset ${rocketStreamVideo.id}", respAssetType)
        }

        return Video(id = rocketStreamVideo.id, title = rocketStreamVideo.title,
                description = rocketStreamVideo.description,
                playbackUrl = rocketStreamVideo.playbackUrl, type = type,
                expirationDate = rocketStreamVideo.expirationDate, assets = assets)
    }

    private fun logUnsuccessResponse(message: String, response: Response<*>) {
        logger.error("rocketstream bad response, $message \t- http ${response.code()} ${response.message()}")
        /*response.errorBody()?.run {
            logger.error("response body: ${this.string()}")
        }*/
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ContainerServiceImpl::class.java)
    }
}

