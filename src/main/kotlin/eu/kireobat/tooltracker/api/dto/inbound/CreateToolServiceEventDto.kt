package eu.kireobat.tooltracker.api.dto.inbound

import java.time.ZonedDateTime
import io.swagger.v3.oas.annotations.media.Schema

data class CreateToolServiceEventDto(
    @Schema(description = "ID refering to the associated damage report", example = "1")
    val damageReportId: Int,
    @Schema(description = "Time when the service event starts", example = "2023-10-01T10:00:00Z")
    val serviceStartTime: ZonedDateTime?,
    @Schema(description = "Time when the service event stops", example = "2023-10-01T12:00:00Z")
    val serviceStopTime: ZonedDateTime?
)
