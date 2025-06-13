package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.inbound.PatchLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.outbound.LendingAgreementDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import eu.kireobat.tooltracker.persistence.entity.toLendingAgreementDto
import eu.kireobat.tooltracker.persistence.repository.LendingAgreementRepository
import eu.kireobat.tooltracker.persistence.repository.ToolRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@Service
class LendingAgreementService(
    private val lendingAgreementRepository: LendingAgreementRepository,
    private val toolService: ToolService,
    private val userService: UserService,
    private val toolRepository: ToolRepository,
) {

    fun create(createLendingAgreementDto: CreateLendingAgreementDto): LendingAgreementEntity {

        val userEntity = userService.findByAuthentication()

        val toolEntity = toolService.findById(createLendingAgreementDto.toolId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find tool with id (${createLendingAgreementDto.toolId})") }

        if (toolEntity.status != ToolStatusEnum.AVAILABLE) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Tool with id (${createLendingAgreementDto.toolId}) is not available right now")
        }

        val lendingAgreementEntity = lendingAgreementRepository.saveAndFlush(
            LendingAgreementEntity(
                borrower = userService.findById(createLendingAgreementDto.borrowerId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user with id (${createLendingAgreementDto.borrowerId})") },
                tool = toolEntity,
                lendingStartTime = createLendingAgreementDto.lendingStartTime ?: ZonedDateTime.now(),
                expectedReturnTime = createLendingAgreementDto.expectedReturnTime ?: ZonedDateTime.now().plusDays(7),
                createdBy = userEntity
            )
        )

        toolEntity.status = ToolStatusEnum.UNAVAILABLE

        toolRepository.saveAndFlush(toolEntity)

        return lendingAgreementEntity
    }

    fun findById(id: Int) = lendingAgreementRepository.findById(id)

    fun findAgreements(pageable: Pageable, toolId: Int?, borrowerId: Int?, lentAfter: ZonedDateTime?, lentBefore: ZonedDateTime?): ToolTrackerPageDto<LendingAgreementDto> {

        val page = lendingAgreementRepository.findAllWithFilter(
            pageable,
            toolId,
            borrowerId,
            lentAfter,
            lentBefore
        )

        return ToolTrackerPageDto(
            page.content.map {entity -> entity.toLendingAgreementDto()},
            page.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )
    }

    fun patch(patchLendingAgreementDto: PatchLendingAgreementDto): LendingAgreementEntity {
        val user = userService.findByAuthentication()

        val lendingAgreementEntity = lendingAgreementRepository.findById(patchLendingAgreementDto.id).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find agreement with id ${patchLendingAgreementDto.id}") }

        if (patchLendingAgreementDto.toolId != null) {
            lendingAgreementEntity.tool = toolService.findById(patchLendingAgreementDto.toolId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find tool with id ${patchLendingAgreementDto.toolId}") }
        }
        if (patchLendingAgreementDto.borrowerId != null) {
            lendingAgreementEntity.borrower = userService.findById(patchLendingAgreementDto.borrowerId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user with id ${patchLendingAgreementDto.borrowerId}") }
        }
        if (patchLendingAgreementDto.lendingStartTime != null) {
            lendingAgreementEntity.lendingStartTime = patchLendingAgreementDto.lendingStartTime
        }
        if (patchLendingAgreementDto.expectedReturnTime != null) {
            lendingAgreementEntity.expectedReturnTime = patchLendingAgreementDto.expectedReturnTime
        }
        if (patchLendingAgreementDto.returnTime != null) {
            lendingAgreementEntity.returnTime = patchLendingAgreementDto.returnTime
        }

        lendingAgreementEntity.modifiedBy = user
        lendingAgreementEntity.modifiedTime = ZonedDateTime.now()

        return lendingAgreementRepository.saveAndFlush(lendingAgreementEntity)
    }
}