package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.inbound.PatchLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.outbound.LendingAgreementDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.persistence.entity.toLendingAgreementDto
import eu.kireobat.tooltracker.service.LendingAgreementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Create a new lending agreement", description = "Creates a new lending agreement based on the provided details. Requires EMPLOYEE role.")
    fun createAgreement(@RequestBody createLendingAgreementDto: CreateLendingAgreementDto): ResponseEntity<LendingAgreementDto> {
        return ResponseEntity.ok(lendingAgreementService.create(createLendingAgreementDto).toLendingAgreementDto())
    }

    @GetMapping("/agreements/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Get a lending agreement by ID", description = "Retrieves a lending agreement by its ID. Requires EMPLOYEE role.")
    fun getAgreement(@PathVariable id: Int): ResponseEntity<LendingAgreementDto> {
        return ResponseEntity.ok(lendingAgreementService.findById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find lending agreement with id ($id)") }.toLendingAgreementDto())
    }

    @GetMapping("/agreements")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Get all lending agreements", description = "Retrieves a paginated list of all lending agreements, optionally filtered by tool ID, borrower ID, or lending date range. Requires EMPLOYEE role.")
    fun getAgreements(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @Parameter(description = "Filter by tool ID", example = "1")
        @RequestParam toolId: Int?,
        @Parameter(description = "Filter by borrower ID", example = "1")
        @RequestParam borrowerId: Int?,
        @Parameter(description = "Filter by lending date after", example = "2023-01-01T00:00:00Z")
        @RequestParam lentAfter: ZonedDateTime?,
        @Parameter(description = "Filter by lending date before", example = "2023-12-31T23:59:59Z")
        @RequestParam lentBefore: ZonedDateTime?,
    ): ResponseEntity<ToolTrackerPageDto<LendingAgreementDto>> {
        return ResponseEntity.ok(lendingAgreementService.findAgreements(pageable, toolId, borrowerId, lentAfter, lentBefore))
    }

    @PatchMapping("/agreements/{id}/patch")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Patch a lending agreement", description = "Updates an existing lending agreement with the provided details. Requires EMPLOYEE role.")
    fun patchAgreement(@RequestBody patchLendingAgreementDto: PatchLendingAgreementDto): ResponseEntity<LendingAgreementDto> {
        return ResponseEntity.ok(lendingAgreementService.patch(patchLendingAgreementDto).toLendingAgreementDto())
    }
}