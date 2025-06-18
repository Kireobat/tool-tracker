package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.outbound.RoleDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.persistence.entity.RoleEntity
import eu.kireobat.tooltracker.persistence.entity.toRoleDto
import eu.kireobat.tooltracker.persistence.repository.RoleRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleService(
    private val roleRepository: RoleRepository,
) {

    fun findRoleById(id: Int): Optional<RoleEntity> {
        return roleRepository.findById(id)
    }

    fun findRoles(pageable: Pageable, name: String?, description: String?): ToolTrackerPageDto<RoleDto> {

        val page = roleRepository.findAllWithFilter(
            pageable,
            name,
            description
        )

        return ToolTrackerPageDto(
            page.content.map {entity -> entity.toRoleDto()},
            page.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )
    }
}