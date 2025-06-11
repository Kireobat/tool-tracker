package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ToolTypeRepository: JpaRepository<ToolTypeEntity, Int> {
    @Query("select type from ToolTypeEntity type " +
            "where " +
            "(:name is null or lower(cast(type.name as string)) like coalesce(concat('%', lower(cast(:name as string)), '%'), '%'))")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("name") name: String?
    ): Page<ToolTypeEntity>
}