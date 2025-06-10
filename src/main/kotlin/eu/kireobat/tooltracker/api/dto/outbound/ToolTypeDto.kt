package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class ToolTypeDto(
    val id: Int,
    val name: String,
    val createdTime: ZonedDateTime,
    val createdBy: UserDto,
    val modifiedTime: ZonedDateTime?,
    val modifiedBy: UserDto?,
)
