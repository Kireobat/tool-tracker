package eu.kireobat.tooltracker.api.dto.inbound

import io.swagger.v3.oas.annotations.media.Schema

data class CreateDamageReportDto(
    @Schema(description = "ID of the lending agreement associated with the damage report", example = "1")
    val lendingAgreementId: Int?,
    @Schema(description = "ID of the tool associated with the damage report", example = "1")
    val toolId: Int?,
    @Schema(description = "Description of the damage", example = "The tool has a broken handle.")
    val description: String
)
