package eu.kireobat.tooltracker.api.dto.outbound

import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import java.time.ZonedDateTime

data class ToolServiceEventDto(
    val id: Int,
    val damageReportDto: DamageReportDto,
    val serviceStartTime: ZonedDateTime,
    val serviceStopTime: ZonedDateTime?,
    val createdTime: ZonedDateTime,
    val createdBy: UserDto,
    val modifiedTime: ZonedDateTime?,
    val modifiedBy: UserDto?,
)
