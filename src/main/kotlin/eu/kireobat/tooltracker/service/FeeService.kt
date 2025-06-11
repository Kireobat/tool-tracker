package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.CreateFeeDto
import eu.kireobat.tooltracker.api.dto.outbound.FeeDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import eu.kireobat.tooltracker.persistence.entity.FeeEntity
import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.entity.toFeeDto
import eu.kireobat.tooltracker.persistence.repository.FeeRepository
import eu.kireobat.tooltracker.persistence.repository.LendingAgreementRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

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

    fun findById(id: Int): Optional<FeeEntity> {
        return feeRepository.findById(id)
    }

    fun findFees(pageable: Pageable, lendingAgreementId: Int? = null, borrowerId: Int? = null, status: FeeStatusEnum? = null, feeAmountMin: Int? = null, feeAmountMax: Int? = null): ToolTrackerPageDto<FeeDto> {
        val page = feeRepository.findAllWithFilter(
            pageable,
            lendingAgreementId,
            borrowerId,
            status,
            feeAmountMin,
            feeAmountMax
        )

        return ToolTrackerPageDto(
            page.content.map {entity -> entity.toFeeDto()},
            page.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )
    }

    fun createFee(createFeeDto: CreateFeeDto, overrideUser: UserEntity? = null): FeeEntity {

        val feeEntity = FeeEntity(
            lendingAgreement = lendingAgreementService.findById(createFeeDto.lendingAgreementId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find agreement with id (${createFeeDto.lendingAgreementId})") },
            reason = createFeeDto.reason,
            feeAmount = createFeeDto.feeAmount,
            createdBy = overrideUser ?: userService.findByAuthentication(),
            status = FeeStatusEnum.UNPAID
        )

        return feeRepository.save(feeEntity)
    }
}