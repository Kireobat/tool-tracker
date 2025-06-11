package eu.kireobat.tooltracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ToolTrackerApplication

fun main(args: Array<String>) {
    runApplication<ToolTrackerApplication>(*args)
}
