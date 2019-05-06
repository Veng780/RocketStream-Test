package veng.takehometest.rocket.model

data class Container(val id: Int,
                     var ads: Collection<Advertisement> = emptyList(),
                     var images: Collection<Image> = emptyList(),
                     var videos: Collection<Video> = emptyList()) {
    val title: String
        get() {
            val titleSb = StringBuilder("container-$id")
            if (ads!!.isNotEmpty()) titleSb.append("_ads")
            if (images!!.isNotEmpty()) titleSb.append("_images")

            titleSb.append("_videos")
            return titleSb.toString()
        }
}
