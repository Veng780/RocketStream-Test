package veng.takehometest.rocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RocketApplication

fun main(args: Array<String>) {
	runApplication<RocketApplication>(*args)
}
