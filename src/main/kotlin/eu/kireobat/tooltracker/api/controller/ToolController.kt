package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.RegisterToolDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import eu.kireobat.tooltracker.persistence.entity.toToolDto
import eu.kireobat.tooltracker.service.ToolService
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
@Tag(name = "Tool endpoints", description = "Endpoints related to managing tools")
class ToolController(
    private val toolService: ToolService,
) {
    @PostMapping("/tools/register")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun registerTool(@RequestBody registerToolDto: RegisterToolDto): ResponseEntity<ToolDto> {
        return ResponseEntity.ok(toolService.registerTool(registerToolDto).toToolDto())
    }

    @GetMapping("/tools/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getTool(
        @PathVariable id: Int
    ): ResponseEntity<ToolDto> {
        return ResponseEntity.ok(toolService.findById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find tool with id ($id)") }.toToolDto())
    }

    @GetMapping("/tools")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getTools(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam name: String?,
        @RequestParam serial: String?,
        @RequestParam toolTypeId: Int?,
        @RequestParam status: ToolStatusEnum?
    ): ResponseEntity<ToolTrackerPageDto<ToolDto>> {
        return ResponseEntity.ok(toolService.findTools(pageable, name, serial, toolTypeId, status))
    }
}