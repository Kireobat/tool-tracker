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
import eu.kireobat.tooltracker.service.UserMapRoleService
import eu.kireobat.tooltracker.service.UserService
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
@Tag(name = "Tool endpoints", description = "Endpoints related to managing tools")
class ToolController(
    private val toolService: ToolService,
    private val userService: UserService,
    private val userMapRoleService: UserMapRoleService,
) {
    @PostMapping("/tools/register")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Register a new tool", description = "Registers a new tool based on the provided details. Requires EMPLOYEE role.")
    fun registerTool(@RequestBody registerToolDto: RegisterToolDto): ResponseEntity<ToolDto> {
        return ResponseEntity.ok(toolService.registerTool(registerToolDto).toToolDto())
    }

    @GetMapping("/tools/{id}")
    @Operation(summary = "Get a tool by ID", description = "Retrieves a tool by its ID. If the tool is not available, access is restricted based on user authentication and role.")
    fun getTool(
        @PathVariable id: Int
    ): ResponseEntity<ToolDto> {

        val toolEntity = toolService.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find tool with id $id") }

        val toolIsNotAvailableAndNoAuth = toolEntity.status != ToolStatusEnum.AVAILABLE && !userService.hasAuthentication()
        val toolIsNotAvailableAndNoPermission = toolEntity.status != ToolStatusEnum.AVAILABLE && userService.hasAuthentication() && !userMapRoleService.isEmployee(userService.findByAuthentication().id)

        if (toolIsNotAvailableAndNoAuth || toolIsNotAvailableAndNoPermission) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied")
        }

        return ResponseEntity.ok(toolEntity.toToolDto())
    }

    @GetMapping("/tools")
    @Operation(summary = "Get all tools", description = "Retrieves a paginated list of all tools, optionally filtered by name, serial number, tool type ID, or status. If the user is not authenticated or does not have the EMPLOYEE role, only available tools are returned.")
    fun getTools(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @Parameter(description = "Filter by tool name (partial match)", example = "Hammer")
        @RequestParam name: String?,
        @Parameter(description = "Filter by tool serial number (partial match)", example = "HM001")
        @RequestParam serial: String?,
        @Parameter(description = "Filter by tool type ID", example = "1")
        @RequestParam toolTypeId: Int?,
        @Parameter(description = "Filter by tool status (if not logged in only AVAILABLE is possible)", example = "AVAILABLE")
        @RequestParam status: ToolStatusEnum?
    ): ResponseEntity<ToolTrackerPageDto<ToolDto>> {

        val statusToUse = if (userService.hasAuthentication() && userMapRoleService.isEmployee(userService.findByAuthentication().id)) {
            status
        } else {
            ToolStatusEnum.AVAILABLE
        }

        return ResponseEntity.ok(toolService.findTools(pageable, name, serial, toolTypeId, statusToUse))
    }
}