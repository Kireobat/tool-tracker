package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.RoleEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<RoleEntity, Int> {
    @Query("select role from RoleEntity role " +
            "where " +
            "(:name is null or lower(cast(role.name as string)) like coalesce(concat('%', lower(cast(:name as string)), '%'), '%')) and " +
            "(:description is null or lower(cast(role.description as string)) like coalesce(concat('%', lower(cast(:description as string)), '%'), '%'))")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("name") name: String?,
        @Param("description") description: String?
    ): Page<RoleEntity>
}