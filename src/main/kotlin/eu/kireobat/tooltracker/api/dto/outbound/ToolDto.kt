package eu.kireobat.tooltracker.api.dto.outbound

import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import java.time.ZonedDateTime

data class ToolDto(
    val id: Int,
    val name: String,
    val serial: String,
    val status: ToolStatusEnum,
    val type: ToolTypeDto,
    val createdTime: ZonedDateTime,
    val createdById: Int,
    val modifiedTime: ZonedDateTime?,
    val modifiedById: Int?,
)
