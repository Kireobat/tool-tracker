package eu.kireobat.tooltracker.api.dto.inbound

import eu.kireobat.tooltracker.common.enums.ToolStatusEnum

data class RegisterToolDto(
    val name: String,
    val serial: String,
    val type: Int,
    val status: ToolStatusEnum?
)
