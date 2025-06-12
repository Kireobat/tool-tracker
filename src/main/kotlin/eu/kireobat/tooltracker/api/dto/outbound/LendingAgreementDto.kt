package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class LendingAgreementDto(
    val id: Int,
    val tool: ToolDto,
    val borrowerId: Int,
    val lendingStartTime: ZonedDateTime,
    val expectedReturnTime: ZonedDateTime,
    val returnTime: ZonedDateTime?,
    val createdById: Int,
    val createdTime: ZonedDateTime,
    val modifiedById: Int?,
    val modifiedTime: ZonedDateTime?,
)
