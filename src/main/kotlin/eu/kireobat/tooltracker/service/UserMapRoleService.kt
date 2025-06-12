package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.PatchUserMapRoleDto
import eu.kireobat.tooltracker.persistence.entity.UserMapRoleEntity
import eu.kireobat.tooltracker.persistence.repository.UserMapRoleRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.getOrElse

@Service
class UserMapRoleService(
    private val userMapRoleRepository: UserMapRoleRepository,
    private val userService: UserService,
    private val roleService: RoleService
) {

    fun getRoles(userId: Int): List<SimpleGrantedAuthority> {
        return userMapRoleRepository.findByUserId(userId).map {e -> SimpleGrantedAuthority(e.role.name) }
    }

    fun create(usersMapRolesDto: PatchUserMapRoleDto): UserMapRoleEntity {

        val userEntity = userService.findByAuthentication()

        val userMapRoleEntity = UserMapRoleEntity(
            user = userService.findById(usersMapRolesDto.userId).getOrElse { throw ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find user with id (${usersMapRolesDto.userId})") },
            role = roleService.getRoleById(usersMapRolesDto.roleId).getOrElse { throw ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find role with id (${usersMapRolesDto.roleId})") },
            createdBy = userEntity,
        )

        return userMapRoleRepository.save(userMapRoleEntity)
    }

    fun delete(usersMapRolesDto: PatchUserMapRoleDto): Int {
        val mappingsToDelete = userMapRoleRepository.findByUserIdAndRoleId(usersMapRolesDto.userId, usersMapRolesDto.roleId)

        userMapRoleRepository.deleteAllById(mappingsToDelete.map { it.id })

        return mappingsToDelete.size
    }
}