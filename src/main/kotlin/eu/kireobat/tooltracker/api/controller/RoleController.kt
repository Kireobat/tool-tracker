package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.PatchUserMapRoleDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.api.dto.outbound.UserMapRoleDto
import eu.kireobat.tooltracker.persistence.entity.toUserMapRoleDto
import eu.kireobat.tooltracker.service.UserMapRoleService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("api/v1")
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Bad Request", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@Tag(name = "Role endpoints", description = "Endpoints related to managing tool types")
class RoleController(
    private val userMapRoleService: UserMapRoleService
) {

    @GetMapping("/roles/give")
    @PreAuthorize("hasRole('ADMIN')")
    fun giveRole(
        @RequestBody patchUserMapRoleDto: PatchUserMapRoleDto
    ): ResponseEntity<UserMapRoleDto> {
        return ResponseEntity.ok(userMapRoleService.create(patchUserMapRoleDto).toUserMapRoleDto())
    }

    @GetMapping("/roles/take")
    @PreAuthorize("hasRole('ADMIN')")
    fun takeRole(
        @RequestBody patchUserMapRoleDto: PatchUserMapRoleDto
    ): ResponseEntity<ToolTrackerResponseDto> {
        val deletedMappings = userMapRoleService.delete(patchUserMapRoleDto)
        return ResponseEntity.ok(ToolTrackerResponseDto(true, ZonedDateTime.now(), HttpStatus.OK, "Deleted ($deletedMappings) mappings"))
    }


}