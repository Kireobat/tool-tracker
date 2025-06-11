package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import eu.kireobat.tooltracker.persistence.entity.FeeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FeeRepository: JpaRepository<FeeEntity, Int> {

    fun findByLendingAgreementId(lendingAgreementId: Int) : List<FeeEntity>

    @Query("select f from FeeEntity f " +
            "where " +
            "(:lendingAgreementId is null or f.lendingAgreement.id = :lendingAgreementId) and " +
            "(:borrowerId is null or f.lendingAgreement.borrower.id = :borrowerId) and " +
            "(:status is null or cast(f.status as string) = cast(:status as string)) and " +
            "(:feeAmountMin is null or f.feeAmount >= :feeAmountMin) and " +
            "(:feeAmountMax is null or f.feeAmount <= :feeAmountMax)")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("lendingAgreementId") lendingAgreementId: Int?,
        @Param("borrowerId") borrowerId: Int?,
        @Param("status") status: FeeStatusEnum?,
        @Param("feeAmountMin") feeAmountMin: Int?,
        @Param("feeAmountMax") feeAmountMax: Int?
    ): Page<FeeEntity>
}