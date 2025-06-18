package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTypeDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.persistence.entity.toToolTypeDto
import eu.kireobat.tooltracker.service.ToolTypeService
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

@RestController
@RequestMapping("api/v1")
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Bad Request", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@Tag(name = "Tool type endpoints", description = "Endpoints related to managing tool types")
class ToolTypeController(
    private val toolTypeService: ToolTypeService,
) {
    @PostMapping("/types/create")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Create a new tool type", description = "Creates a new tool type based on the provided name. Requires EMPLOYEE role.")
    fun createToolType(@RequestParam name: String): ResponseEntity<ToolTypeDto> {
        return ResponseEntity.ok(toolTypeService.create(name).toToolTypeDto())
    }

    @GetMapping("/types/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Get a tool type by ID", description = "Retrieves a tool type by its ID. Requires EMPLOYEE role.")
    fun getToolType(
        @PathVariable id: Int
    ): ResponseEntity<ToolTypeDto> {
        return ResponseEntity.ok(toolTypeService.findById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find toolType with id ($id)") }.toToolTypeDto())
    }

    @GetMapping("/types")
    @Operation(summary = "Get all tool types", description = "Retrieves a paginated list of all tool types, optionally filtered by name. Does not require authentication.")
    fun getToolTypes(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @Parameter(description = "Filter by tool type name (partial match)", example = "Hammer")
        @RequestParam name: String?
    ): ResponseEntity<ToolTrackerPageDto<ToolTypeDto>> {
        return ResponseEntity.ok(toolTypeService.findToolTypes(pageable, name))
    }
}