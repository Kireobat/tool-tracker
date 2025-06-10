package eu.kireobat.tooltracker

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<ToolTrackerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
