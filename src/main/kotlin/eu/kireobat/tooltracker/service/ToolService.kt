package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.RegisterToolDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import eu.kireobat.tooltracker.persistence.entity.*
import eu.kireobat.tooltracker.persistence.repository.ToolRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ToolService(
    private val toolRepository: ToolRepository,
    private val toolTypeService: ToolTypeService,
    private val userService: UserService,
) {

    fun registerTool(registerToolDto: RegisterToolDto): ToolEntity {
        val userEntity = userService.findByAuthentication()

        return toolRepository.saveAndFlush(ToolEntity(
            name = registerToolDto.name,
            serial = registerToolDto.serial,
            type = toolTypeService.findById(registerToolDto.type).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find tool type with id: ${registerToolDto.type}") },
            createdBy = userEntity
        ))
    }

    fun findById(toolId: Int): Optional<ToolEntity> {
        return toolRepository.findById(toolId)
    }

    fun findTools(pageable: Pageable, name: String?, serial: String?, toolTypeId: Int?, status: ToolStatusEnum?): ToolTrackerPageDto<ToolDto> {

        val page = toolRepository.findAllWithFilter(
            pageable,
            name,
            serial,
            toolTypeId,
            status
        )

        return ToolTrackerPageDto(
            page.content.map {entity -> entity.toToolDto()},
            page.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )

    }
}