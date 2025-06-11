package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.CreateToolServiceEventDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolServiceEventDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.persistence.entity.ToolServiceEventEntity
import eu.kireobat.tooltracker.persistence.entity.toToolServiceEventDto
import eu.kireobat.tooltracker.persistence.repository.ToolServiceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Service
class ToolServiceEventService(
    private val toolServiceRepository: ToolServiceRepository,
    private val userService: UserService,
    private val damageReportService: DamageReportService
) {

    fun create(createToolServiceEventDto: CreateToolServiceEventDto): ToolServiceEventEntity {
        val userEntity = userService.findByAuthentication()

        return toolServiceRepository.saveAndFlush(ToolServiceEventEntity(
            damageReport = damageReportService.findById(createToolServiceEventDto.damageReportId).orElseThrow { throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Could not find damage report with id (${createToolServiceEventDto.damageReportId})") },
            serviceStartTime = createToolServiceEventDto.serviceStartTime,
            serviceStopTime = createToolServiceEventDto.serviceStopTime,
            createdBy = userEntity
        ))
    }

    fun findById(id: Int): Optional<ToolServiceEventEntity> {
        return toolServiceRepository.findById(id)
    }

    fun findServiceEvents(pageable: Pageable, toolId: Int?, damageReportId: Int?, lendingAgreementId: Int?, searchPeriodStart: ZonedDateTime?, searchPeriodStop: ZonedDateTime?): ToolTrackerPageDto<ToolServiceEventDto> {


        val page = toolServiceRepository.findAllWithFilter(
            pageable,
            toolId,
            damageReportId,
            lendingAgreementId,
            searchPeriodStart,
            searchPeriodStop
        )

        return ToolTrackerPageDto(
            page.content.map {entity -> entity.toToolServiceEventDto()},
            page.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )
    }

}