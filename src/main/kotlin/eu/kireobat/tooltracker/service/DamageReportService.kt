package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.CreateDamageReportDto
import eu.kireobat.tooltracker.persistence.entity.DamageReportEntity
import eu.kireobat.tooltracker.persistence.repository.DamageReportRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class DamageReportService(
    private val damageReportRepository: DamageReportRepository,
    private val lendingAgreementService: LendingAgreementService,
    private val toolService: ToolService,
    private val userService: UserService
) {

    fun create(createDamageReportDto: CreateDamageReportDto): DamageReportEntity {

        val userEntity = userService.findByAuthentication()

        // TODO validate?

        if (createDamageReportDto.lendingAgreementId == null && createDamageReportDto.toolId == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "lendingAgreementId and toolId can not be null at the same time")
        }


        return damageReportRepository.saveAndFlush(DamageReportEntity(
            lendingAgreement = if (createDamageReportDto.lendingAgreementId != null) {lendingAgreementService.findById(createDamageReportDto.lendingAgreementId).get()} else {null},
            tool = if (createDamageReportDto.toolId != null) {toolService.findById(createDamageReportDto.toolId).get()} else {null},
            description = createDamageReportDto.description,
            createdBy = userEntity

        ))
    }

    fun findById(id: Int): Optional<DamageReportEntity> {
        return damageReportRepository.findById(id)
    }
}