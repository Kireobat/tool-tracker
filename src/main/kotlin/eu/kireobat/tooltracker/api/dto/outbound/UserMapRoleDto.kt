package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class UserMapRoleDto(
    val id: Int,
    val userId: Int,
    val roleId: Int,
    val createdById: Int,
    val createdTime: ZonedDateTime,
    val modifiedById: Int?,
    val modifiedTime: ZonedDateTime?,
)
