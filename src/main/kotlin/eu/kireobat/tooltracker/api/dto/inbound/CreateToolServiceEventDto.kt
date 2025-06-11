package eu.kireobat.tooltracker.api.dto.inbound

import java.time.ZonedDateTime

data class CreateToolServiceEventDto(
    val damageReportId: Int,
    val serviceStartTime: ZonedDateTime?,
    val serviceStopTime: ZonedDateTime?
)
