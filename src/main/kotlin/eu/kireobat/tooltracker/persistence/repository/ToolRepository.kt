package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ToolRepository: JpaRepository<ToolEntity, Int> {
    @Query("select t from ToolEntity t " +
            "where " +
            "(:name is null or lower(cast(t.name as string)) like coalesce(concat('%', lower(cast(:name as string)), '%'), '%')) and " +
            "(:serial is null or lower(cast(t.serial as string)) like coalesce(concat('%', lower(cast(:serial as string)), '%'), '%')) and " +
            "(:toolTypeId is null or t.type.id = :toolTypeId) and " +
            "(:status is null or cast(t.status as string) = cast(:status as string))")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("name") name: String?,
        @Param("serial") serial: String?,
        @Param("toolTypeId") toolTypeId: Int?,
        @Param("status") status: ToolStatusEnum?
    ): Page<ToolEntity>
}