package veng.takehometest.rocket.model

enum class TypeOfVideo {CLIP, EPISODE, MOVIE}

enum class AsserType {AD, IMAGE}

data class Asset(val assetId: Int, val assertType: AsserType)

data class Video(val id: Int, val title: String,
            val description: String?,
            val playbackUrl: String,
            val type: TypeOfVideo?,
            val expirationDate: String?,
            val assets: List<Asset>?)