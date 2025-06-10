package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.RegisterToolDto
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import eu.kireobat.tooltracker.persistence.repository.ToolRepository
import eu.kireobat.tooltracker.persistence.repository.ToolServiceRepository
import eu.kireobat.tooltracker.persistence.repository.ToolTypeRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ToolService(
    private val toolRepository: ToolRepository,
    private val toolTypeRepository: ToolTypeRepository,
    private val toolServiceRepository: ToolServiceRepository,
    private val userService: UserService
) {

    fun registerTool(registerToolDto: RegisterToolDto): ToolEntity {
        val userEntity = userService.findByAuthentication()

        return toolRepository.saveAndFlush(ToolEntity(
            name = registerToolDto.name,
            serial = registerToolDto.serial,
            type = findToolTypeById(registerToolDto.type).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find tool type with id: ${registerToolDto.type}") },
            createdBy = userEntity
        ))
    }

    fun createToolType(name: String): ToolTypeEntity {
        val userEntity = userService.findByAuthentication()

        return toolTypeRepository.saveAndFlush(ToolTypeEntity(
            name = name,
            createdBy = userEntity,
        ))
    }

    fun findToolById(toolId: Int): Optional<ToolEntity> {
        return toolRepository.findById(toolId)
    }

    fun findToolTypeById(toolTypeId: Int): Optional<ToolTypeEntity> {
        return toolTypeRepository.findById(toolTypeId)
    }
}