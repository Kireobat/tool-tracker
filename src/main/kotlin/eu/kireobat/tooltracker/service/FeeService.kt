package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import eu.kireobat.tooltracker.persistence.entity.FeeEntity
import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.FeeRepository
import eu.kireobat.tooltracker.persistence.repository.LendingAgreementRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FeeService(
    private val lendingAgreementRepository: LendingAgreementRepository,
    private val feeRepository: FeeRepository,
    private val lendingAgreementService: LendingAgreementService,
    private val userService: UserService
) {

    fun findOffendingLendingAgreements(): List<LendingAgreementEntity> {
        return lendingAgreementRepository.findOffendingLendingAgreements()
    }

    fun findFeesByLendingAgreementId(lendingAgreementId: Int): List<FeeEntity> {
        return feeRepository.findByLendingAgreementId(lendingAgreementId)
    }

    fun createFee(lendingAgreementId: Int, reason: String, feeAmount: Int, overrideUser: UserEntity? = null): FeeEntity {

        val feeEntity = FeeEntity(
            lendingAgreement = lendingAgreementService.findById(lendingAgreementId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find agreement with id ($lendingAgreementId)") },
            reason = reason,
            feeAmount = feeAmount,
            createdBy = overrideUser ?: userService.findByAuthentication(),
            status = FeeStatusEnum.UNPAID
        )

        return feeRepository.save(feeEntity)
    }
}