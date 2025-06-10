package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.persistence.entity.RoleEntity
import eu.kireobat.tooltracker.persistence.repository.RoleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleService(
    private val roleRepository: RoleRepository,
) {

    fun getRoleById(id: Int): Optional<RoleEntity> {
        return roleRepository.findById(id)
    }
}