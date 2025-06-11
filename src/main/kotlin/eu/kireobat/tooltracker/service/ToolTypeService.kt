package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTypeDto
import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import eu.kireobat.tooltracker.persistence.entity.toToolTypeDto
import eu.kireobat.tooltracker.persistence.repository.ToolTypeRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
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

    fun findToolTypes(pageable: Pageable, name: String?): ToolTrackerPageDto<ToolTypeDto> {

        val page = toolTypeRepository.findAllWithFilter(
            pageable,
            name,
        )

        return ToolTrackerPageDto(
            page.content.map {entity -> entity.toToolTypeDto()},
            page.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )

    }
}