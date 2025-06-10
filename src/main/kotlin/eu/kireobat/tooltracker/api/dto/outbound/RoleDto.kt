package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class RoleDto(
    val id: Int,
    val name: String,
    val description: String,
    val createdTime: ZonedDateTime,
    val createdBy: UserDto,
    val modifiedTime: ZonedDateTime?,
    val modifiedBy: UserDto?,
)