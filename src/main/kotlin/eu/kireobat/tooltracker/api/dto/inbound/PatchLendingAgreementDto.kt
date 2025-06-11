package eu.kireobat.tooltracker.api.dto.inbound

import java.time.ZonedDateTime

data class PatchLendingAgreementDto (
    val id: Int,
    val toolId: Int? = null,
    val borrowerId: Int? = null,
    val lendingStartTime: ZonedDateTime? = null,
    val expectedReturnTime: ZonedDateTime? = null,
    val returnTime: ZonedDateTime? = null,
    )