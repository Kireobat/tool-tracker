package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface LendingAgreementRepository: JpaRepository<LendingAgreementEntity, Int> {

    @Query("select la from LendingAgreementEntity la " +
            "where " +
            "(:toolId is null or la.tool.id = :toolId) and " +
            "(:borrowerId is null or la.borrower.id = :borrowerId) and " +
            "(cast(:lentAfter as string) is null or la.lendingStartTime >= :lentAfter) and " +
            "(cast(:lentBefore as string) is null or la.lendingStartTime <= :lentBefore)")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("toolId") toolId: Int?,
        @Param("borrowerId") borrowerId: Int?,
        @Param("lentAfter") lentAfter: ZonedDateTime?,
        @Param("lentBefore") lentBefore: ZonedDateTime?
    ): Page<LendingAgreementEntity>
}