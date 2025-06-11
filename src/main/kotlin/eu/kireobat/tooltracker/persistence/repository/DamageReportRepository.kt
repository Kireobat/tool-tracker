package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.DamageReportEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DamageReportRepository: JpaRepository<DamageReportEntity, Int> {
    @Query("select dr from DamageReportEntity dr " +
            "where " +
            "(:lendingAgreementId is null or dr.lendingAgreement.id = :lendingAgreementId) and " +
            "(:toolId is null or dr.tool.id = :toolId)")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("lendingAgreementId") lendingAgreementId: Int?,
        @Param("toolId") toolId: Int?
    ): Page<DamageReportEntity>
}