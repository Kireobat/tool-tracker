package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.CreateDamageReportDto
import eu.kireobat.tooltracker.api.dto.outbound.DamageReportDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.persistence.entity.toDamageReportDto
import eu.kireobat.tooltracker.service.DamageReportService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1")
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Bad Request", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@Tag(name = "Damage report endpoints", description = "Endpoints related to managing damage reports")
class DamageReportController(
    private val damageReportService: DamageReportService,
) {

    @PostMapping("/reports/create")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun createReport(@RequestBody createDamageReportDto: CreateDamageReportDto): ResponseEntity<DamageReportDto> {
        return ResponseEntity.ok(damageReportService.create(createDamageReportDto).toDamageReportDto())
    }

    @GetMapping("/reports/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getReport(@PathVariable id: Int): ResponseEntity<DamageReportDto> {
        return ResponseEntity.ok(damageReportService.findById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find damage report with id ($id)") }.toDamageReportDto())
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getReports(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam lendingAgreementId: Int?,
        @RequestParam toolId: Int?,
    ): ResponseEntity<ToolTrackerPageDto<DamageReportDto>> {
        return ResponseEntity.ok(damageReportService.findReports(pageable, lendingAgreementId, toolId))
    }
}