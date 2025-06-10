package eu.kireobat.tooltracker.api.dto.outbound

import java.time.ZonedDateTime

data class DamageReportDto(
    val id: Int,
    val lendingAgreement: LendingAgreementDto?,
    val tool: ToolDto?,
    val description: String?,
    val createdTime: ZonedDateTime,
    val createdBy: UserDto,
    val modifiedTime: ZonedDateTime?,
    val modifiedBy: UserDto?,
)
