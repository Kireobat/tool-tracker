package eu.kireobat.tooltracker.api.dto.inbound

import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import io.swagger.v3.oas.annotations.media.Schema

data class RegisterToolDto(
    @Schema(description = "Name of the tool", example = "Hammer")
    val name: String,
    @Schema(description = "Serial number of the tool", example = "SN123456")
    val serial: String,
    @Schema(description = "Type of the tool (must be an ID from toolTypes)", example = "1")
    val type: Int,
    @Schema(description = "Status of the tool", example = "AVAILABLE")
    val status: ToolStatusEnum? = null
)
