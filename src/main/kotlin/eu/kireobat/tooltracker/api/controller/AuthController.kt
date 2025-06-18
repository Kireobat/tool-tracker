package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.api.dto.outbound.UserDto
import eu.kireobat.tooltracker.persistence.entity.toUserDto
import eu.kireobat.tooltracker.service.UserMapRoleService
import eu.kireobat.tooltracker.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Bad Request", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@Tag(name = "Auth endpoints", description = "Endpoints related auth")
class AuthController(
    private val userMapRoleService: UserMapRoleService,
    private val userService: UserService
) {

    @GetMapping("/auth/isEmployee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Check if the authenticated user is an employee", description = "Returns true if the authenticated user has the EMPLOYEE role, 401 if not.")
    fun isEmployee(): ResponseEntity<Boolean> {
        val userEntity = userService.findByAuthentication()
        return ResponseEntity.ok(userMapRoleService.isEmployee(userEntity.id))
    }

    @GetMapping("/auth/isAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check if the authenticated user is an admin", description = "Returns true if the authenticated user has the ADMIN role, 401 if not.")
    fun isAdmin(): ResponseEntity<Boolean> {
        val userEntity = userService.findByAuthentication()
        return ResponseEntity.ok(userMapRoleService.isAdmin(userEntity.id))
    }

    @GetMapping("/auth/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get the profile of the authenticated user", description = "Returns the profile information of the authenticated user.")
    fun getProfile(): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.findByAuthentication().toUserDto())
    }
}