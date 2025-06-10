package eu.kireobat.tooltracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ToolTrackerApplication

fun main(args: Array<String>) {
    runApplication<ToolTrackerApplication>(*args)
}
