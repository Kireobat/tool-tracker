package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.persistence.entity.UserMapRoleEntity
import eu.kireobat.tooltracker.persistence.repository.UserMapRoleRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
class UserMapRoleService(
    private val userMapRoleRepository: UserMapRoleRepository
) {

    fun getRoles(userId: Int): List<SimpleGrantedAuthority> {
        return userMapRoleRepository.findByUserId(userId).map {e -> SimpleGrantedAuthority(e.role.name) }
    }

    fun create(userMapRoleEntity: UserMapRoleEntity): UserMapRoleEntity {
        return userMapRoleRepository.save(userMapRoleEntity)
    }
}