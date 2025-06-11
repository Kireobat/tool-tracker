package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.RegisterToolDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.persistence.entity.toToolDto
import eu.kireobat.tooltracker.service.ToolService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    @PreAuthorize("hasRole('USER')")
    fun registerTool(@RequestBody registerToolDto: RegisterToolDto): ResponseEntity<ToolDto> {
        return ResponseEntity.ok(toolService.registerTool(registerToolDto).toToolDto())
    }
}