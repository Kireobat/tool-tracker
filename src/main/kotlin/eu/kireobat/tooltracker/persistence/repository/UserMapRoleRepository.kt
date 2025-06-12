package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.UserMapRoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserMapRoleRepository: JpaRepository<UserMapRoleEntity, Int> {
    fun findByUserId(userId: Int): List<UserMapRoleEntity>

    fun findByUserIdAndRoleId(userId: Int, roleId: Int): List<UserMapRoleEntity>
}