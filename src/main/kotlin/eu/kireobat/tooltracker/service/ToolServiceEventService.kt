package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.CreateToolServiceEventDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolServiceEventDto
import eu.kireobat.tooltracker.persistence.entity.ToolServiceEventEntity
import eu.kireobat.tooltracker.persistence.entity.toToolServiceEventDto
import eu.kireobat.tooltracker.persistence.repository.ToolServiceRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ToolServiceEventService(
    private val toolServiceRepository: ToolServiceRepository,
    private val userService: UserService,
    private val damageReportService: DamageReportService
) {

    fun createToolServiceEvent(createToolServiceEventDto: CreateToolServiceEventDto): ToolServiceEventDto {
        val userEntity = userService.findByAuthentication()

        return toolServiceRepository.saveAndFlush(ToolServiceEventEntity(
            damageReport = damageReportService.findById(createToolServiceEventDto.damageReportId).orElseThrow { throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Could not find damage report with id (${createToolServiceEventDto.damageReportId})") },
            serviceStartTime = createToolServiceEventDto.serviceStartTime,
            serviceStopTime = createToolServiceEventDto.serviceStopTime,
            createdBy = userEntity
        )).toToolServiceEventDto()
    }

}