package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.ToolServiceEventEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface ToolServiceRepository: JpaRepository<ToolServiceEventEntity, Int> {

    @Query("select tse from ToolServiceEventEntity tse " +
            "left join DamageReportEntity dr on tse.damageReport.id = dr.id " +
            "left join LendingAgreementEntity la on tse.damageReport.lendingAgreement.id = la.id " +
            "where " +
            "(:toolId is null or dr.tool.id = :toolId) and " +
            "(:damageReportId is null or dr.id = :damageReportId) and " +
            "(:lendingAgreementId is null or la.id = :lendingAgreementId) and " +
            "(tse.serviceStartTime >= :searchPeriodStart) and " +
            "(tse.serviceStopTime <= :searchPeriodStop)")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("toolId") toolId: Int?,
        @Param("damageReportId") damageReportId: Int?,
        @Param("lendingAgreementId") lendingAgreementId: Int?,
        @Param("searchPeriodStart") searchPeriodStart: ZonedDateTime,
        @Param("searchPeriodStop") searchPeriodStop: ZonedDateTime
        ): Page<ToolServiceEventEntity>
}