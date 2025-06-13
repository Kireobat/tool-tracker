package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.PatchUserMapRoleDto
import eu.kireobat.tooltracker.api.dto.outbound.RoleDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.api.dto.outbound.UserMapRoleDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.persistence.entity.toRoleDto
import eu.kireobat.tooltracker.persistence.entity.toUserMapRoleDto
import eu.kireobat.tooltracker.service.RoleService
import eu.kireobat.tooltracker.service.UserMapRoleService
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
@Tag(name = "Role endpoints", description = "Endpoints related to managing tool types")
class RoleController(
    private val userMapRoleService: UserMapRoleService,
    private val roleService: RoleService
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

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getRole(
        @PathVariable id: Int
    ): ResponseEntity<RoleDto> {
        return ResponseEntity.ok(roleService.findRoleById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find toolType with id ($id)") }.toRoleDto())
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getRoles(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam name: String?,
        @RequestParam email: String?
    ): ResponseEntity<ToolTrackerPageDto<RoleDto>> {
        return ResponseEntity.ok(roleService.findRoles(pageable, name, email))
    }
}