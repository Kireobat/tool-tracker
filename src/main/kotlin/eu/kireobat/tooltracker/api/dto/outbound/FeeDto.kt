package eu.kireobat.tooltracker.api.dto.outbound

import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import java.time.ZonedDateTime

data class FeeDto(
    val id: Int,
    val lendingAgreement: LendingAgreementDto,
    val reason: String,
    val feeAmount: Int,
    val status: FeeStatusEnum,
    val createdTime: ZonedDateTime,
    val createdById: Int,
    val modifiedTime: ZonedDateTime?,
    val modifiedById: Int?,
)
