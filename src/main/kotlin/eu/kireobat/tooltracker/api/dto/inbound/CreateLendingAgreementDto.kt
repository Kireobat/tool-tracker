package eu.kireobat.tooltracker.api.dto.inbound

import java.time.ZonedDateTime

data class CreateLendingAgreementDto(
    val toolId: Int,
    val borrowerId: Int,
    val lendingStartTime: ZonedDateTime? = null,
    val expectedReturnTime: ZonedDateTime? = null,
)
