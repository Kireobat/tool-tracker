package eu.kireobat.tooltracker.api.dto.inbound

import io.swagger.v3.oas.annotations.media.Schema
import java.time.ZonedDateTime

data class PatchLendingAgreementDto (
    @Schema(description = "ID of the lending agreement to be patched", example = "1")
    val id: Int,
    @Schema(description = "ID of the tool being lent out", example = "1")
    val toolId: Int? = null,
    @Schema(description = "ID of the borrower", example = "1")
    val borrowerId: Int? = null,
    @Schema(description = "Time when the lending starts", example = "2023-10-01T10:00:00Z")
    val lendingStartTime: ZonedDateTime? = null,
    @Schema(description = "Expected return time for the tool", example = "2023-10-15T10:00:00Z")
    val expectedReturnTime: ZonedDateTime? = null,
    @Schema(description = "Actual return time for the tool", example = "2023-10-15T10:00:00Z")
    val returnTime: ZonedDateTime? = null,
    )