package veng.takehometest.rocket.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import veng.takehometest.rocket.model.*
import veng.takehometest.rocket.service.ContainerService
import java.util.*

@RestController
@RequestMapping("containers")
class ContainerController {
    @Autowired
    lateinit var service: ContainerService

    @GetMapping("/")
    fun getContainers(): ResponseEntity<Collection<Container>> {
        return ResponseEntity.ok(service.getContainers())
    }

    @GetMapping("/{containerId}")
    fun getContainer(@PathVariable("containerId") containerId: Int): ResponseEntity<Container> {
        val container = service.getContainer(containerId)
        if (container.videos!!.isEmpty()) {
            // spec:    A container without video associations should not be listed
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok<Container>(container)
    }

    @GetMapping("/{containerId}/ads")
    fun getAdvertisements(@PathVariable("containerId") containerId: Int): ResponseEntity<SimpleContainer<Advertisement>> {
        val entity = SimpleContainer(containerId, "advertisements", service.getAdvertisements(containerId))
        return ResponseEntity.ok(entity)
    }

    @GetMapping("/{containerId}/images")
    fun getImages(@PathVariable("containerId") containerId: Int): ResponseEntity<SimpleContainer<Image>> {
        val entity = SimpleContainer(containerId, "images", service.getImages(containerId))
        return ResponseEntity.ok(entity)
    }

    @GetMapping("/{containerId}/videos")
    fun getVideos(@PathVariable("containerId") containerId: Int): ResponseEntity<SimpleContainer<Video>> {
        val entity = SimpleContainer(containerId, "videos", service.getVideos(containerId))
        return ResponseEntity.ok(entity)
    }

}