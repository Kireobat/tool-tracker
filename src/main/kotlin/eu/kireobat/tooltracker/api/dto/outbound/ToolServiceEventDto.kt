package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class ToolServiceEventDto(
    val id: Int,
    val damageReportDto: DamageReportDto,
    val serviceStartTime: ZonedDateTime,
    val serviceStopTime: ZonedDateTime?,
    val createdTime: ZonedDateTime,
    val createdById: Int,
    val modifiedTime: ZonedDateTime?,
    val modifiedById: Int?,
)
