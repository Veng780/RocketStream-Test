package veng.takehometest.rocket.repository.rocketstream

class RocketStreamAdvertisement(val id: Int, val name: String?, val url: String, val containerId: Int?)
class AdvertisementsWrapper(val advertisements: List<RocketStreamAdvertisement>)


class RocketStreamImage(val id: Int, val name: String, val url: String, val containerId: Int?)
class ImagesWrapper(val images: List<RocketStreamImage>)

enum class RocketStreamVideoType {clip, episode, movie}

class RocketStreamVideo(val id: Int, val title: String, val description: String?,
                        val playbackUrl: String,
                        val type: String,
                        val expirationDate: String?,
                        val containerId: Int?)
class VideosWrapper(val videos: List<RocketStreamVideo>)

enum class RocketStreamAssetType {ad, image}

class RocketStreamVideoAsset(val videoId: Int, val assetId: Int, assetType: RocketStreamAssetType)
class VideoAssetsWrapper(val assetTypes: List<RocketStreamVideoAsset>)