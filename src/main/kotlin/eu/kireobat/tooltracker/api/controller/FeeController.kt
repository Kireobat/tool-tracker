package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.CreateFeeDto
import eu.kireobat.tooltracker.api.dto.outbound.FeeDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import eu.kireobat.tooltracker.persistence.entity.toFeeDto
import eu.kireobat.tooltracker.service.FeeService
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
@Tag(name = "Fee endpoints", description = "Endpoints related to managing fees")
class FeeController(
    private val feeService: FeeService,
) {
    @PostMapping("/fees/create")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun registerTool(@RequestBody createFeeDto: CreateFeeDto): ResponseEntity<FeeDto> {
        return ResponseEntity.ok(feeService.createFee(createFeeDto).toFeeDto())
    }

    @GetMapping("/fees/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getTool(
        @PathVariable id: Int
    ): ResponseEntity<FeeDto> {
        return ResponseEntity.ok(feeService.findById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find fee with id ($id)") }.toFeeDto())
    }

    @GetMapping("/fees")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getFees(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam lendingAgreementId: Int? = null,
        @RequestParam borrowerId: Int? = null,
        @RequestParam status: FeeStatusEnum? = null,
        @RequestParam feeAmountMin: Int? = null,
        @RequestParam feeAmountMax: Int? = null
    ): ResponseEntity<ToolTrackerPageDto<FeeDto>> {
        return ResponseEntity.ok(feeService.findFees(pageable, lendingAgreementId, borrowerId, status, feeAmountMin, feeAmountMax))
    }
}