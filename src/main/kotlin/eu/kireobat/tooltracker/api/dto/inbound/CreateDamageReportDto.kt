package eu.kireobat.tooltracker.api.dto.inbound

data class CreateDamageReportDto(
    val lendingAgreementId: Int?,
    val toolId: Int?,
    val description: String
)
