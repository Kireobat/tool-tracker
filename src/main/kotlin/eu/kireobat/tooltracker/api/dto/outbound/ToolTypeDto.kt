package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class ToolTypeDto(
    val id: Int,
    val name: String,
    val createdTime: ZonedDateTime,
    val createdById: Int,
    val modifiedTime: ZonedDateTime?,
    val modifiedById: Int?,
)
