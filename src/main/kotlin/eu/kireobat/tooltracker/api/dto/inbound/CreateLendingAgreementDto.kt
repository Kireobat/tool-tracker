package eu.kireobat.tooltracker.api.dto.inbound

import java.time.ZonedDateTime
import io.swagger.v3.oas.annotations.media.Schema

data class CreateLendingAgreementDto(
    @Schema(description = "ID of the tool being lent out", example = "1")
    val toolId: Int,
    @Schema(description = "ID of the borrower", example = "1")
    val borrowerId: Int,
    @Schema(description = "Time when the lending starts", example = "2023-10-01T10:00:00Z")
    val lendingStartTime: ZonedDateTime? = null,
    @Schema(description = "Expected return time for the tool", example = "2023-10-15T10:00:00Z")
    val expectedReturnTime: ZonedDateTime? = null,
)
