package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val createdTime: ZonedDateTime,
)