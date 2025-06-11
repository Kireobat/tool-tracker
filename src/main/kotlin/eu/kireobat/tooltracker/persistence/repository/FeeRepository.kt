package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.FeeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeeRepository: JpaRepository<FeeEntity, Int> {

    fun findByLendingAgreementId(lendingAgreementId: Int) : List<FeeEntity>
}