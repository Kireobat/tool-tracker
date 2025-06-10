package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class LendingAgreementDto(
    val id: Int,
    val tool: ToolDto,
    val borrower: UserDto,
    val lendingStartTime: ZonedDateTime,
    val expectedReturnTime: ZonedDateTime,
    val returnTime: ZonedDateTime?,
    val createdBy: UserDto,
    val createdTime: ZonedDateTime,
    val modifiedBy: UserDto?,
    val modifiedTime: ZonedDateTime?,
)
