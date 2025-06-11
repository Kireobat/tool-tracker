package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.CreateDamageReportDto
import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.inbound.RegisterToolDto
import eu.kireobat.tooltracker.api.dto.outbound.DamageReportDto
import eu.kireobat.tooltracker.api.dto.outbound.LendingAgreementDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import eu.kireobat.tooltracker.persistence.entity.toLendingAgreementDto
import eu.kireobat.tooltracker.service.DamageReportService
import eu.kireobat.tooltracker.service.LendingAgreementService
import eu.kireobat.tooltracker.service.ToolService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Bad Request", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@Tag(name = "Lending agreement endpoints", description = "Endpoints related to managing lending agreements")
class LendingAgreementController(
    private val lendingAgreementService: LendingAgreementService,
) {

    @PostMapping("/agreements/create")
    @PreAuthorize("hasRole('USER')")
    fun createAgreement(@RequestBody createLendingAgreementDto: CreateLendingAgreementDto): ResponseEntity<LendingAgreementDto> {
        return ResponseEntity.ok(lendingAgreementService.create(createLendingAgreementDto).toLendingAgreementDto())
    }
}