package eu.kireobat.tooltracker.api.dto

import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

data class ToolTrackerResponseDto (
    var success: Boolean,
    var timestamp: ZonedDateTime,
    var status: HttpStatus,
    var message: String
)