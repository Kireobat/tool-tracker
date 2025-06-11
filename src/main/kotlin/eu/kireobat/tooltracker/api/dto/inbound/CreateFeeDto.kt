package eu.kireobat.tooltracker.api.dto.inbound

data class CreateFeeDto(
    val lendingAgreementId: Int,
    val reason: String,
    val feeAmount: Int
)
