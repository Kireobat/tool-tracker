package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.outbound.LendingAgreementDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.persistence.entity.toLendingAgreementDto
import eu.kireobat.tooltracker.service.LendingAgreementService
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
import java.time.ZonedDateTime

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

    @GetMapping("/agreements/{id}")
    fun getAgreement(@PathVariable id: Int): ResponseEntity<LendingAgreementDto> {
        return ResponseEntity.ok(lendingAgreementService.findById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find lending agreement with id ($id)") }.toLendingAgreementDto())
    }

    @GetMapping("/agreements")
    fun getAgreements(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam toolId: Int?,
        @RequestParam borrowerId: Int?,
        @RequestParam lentAfter: ZonedDateTime?,
        @RequestParam lentBefore: ZonedDateTime?,
    ): ResponseEntity<ToolTrackerPageDto<LendingAgreementDto>> {
        return ResponseEntity.ok(lendingAgreementService.findAgreements(pageable, toolId, borrowerId, lentAfter, lentBefore))
    }
}