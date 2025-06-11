package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.CreateToolServiceEventDto
import eu.kireobat.tooltracker.api.dto.inbound.RegisterToolDto
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.ToolServiceEventEntity
import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import eu.kireobat.tooltracker.persistence.repository.ToolRepository
import eu.kireobat.tooltracker.persistence.repository.ToolServiceRepository
import eu.kireobat.tooltracker.persistence.repository.ToolTypeRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ToolTypeService(
    private val toolTypeRepository: ToolTypeRepository,
    private val userService: UserService,
) {

    fun create(name: String): ToolTypeEntity {
        val userEntity = userService.findByAuthentication()

        return toolTypeRepository.saveAndFlush(ToolTypeEntity(
            name = name,
            createdBy = userEntity,
        ))
    }
    fun findById(id: Int): Optional<ToolTypeEntity> {
        return toolTypeRepository.findById(id)
    }
}