package eu.kireobat.tooltracker.api.dto.inbound

import io.swagger.v3.oas.annotations.media.Schema

data class CreateFeeDto(
    @Schema(description = "ID of the lending agreement associated with the fee", example = "1")
    val lendingAgreementId: Int,
    @Schema(description = "Reason for the fee", example = "Late return of tool")
    val reason: String,
    @Schema(description = "Fee amount in NOK", example = "1000")
    val feeAmount: Int
)
