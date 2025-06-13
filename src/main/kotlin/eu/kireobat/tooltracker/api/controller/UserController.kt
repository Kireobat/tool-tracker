package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.inbound.LoginDto
import eu.kireobat.tooltracker.api.dto.inbound.PatchUserMapRoleDto
import eu.kireobat.tooltracker.api.dto.inbound.RegisterUserDto
import eu.kireobat.tooltracker.api.dto.inbound.validate
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerPageDto
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.api.dto.outbound.UserDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.tooltracker.persistence.entity.toUserDto
import eu.kireobat.tooltracker.service.RoleService
import eu.kireobat.tooltracker.service.UserMapRoleService
import eu.kireobat.tooltracker.service.UserService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@RestController
@RequestMapping("api/v1")
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Bad Request", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@Tag(name = "User endpoints", description = "Endpoints related to registering and logging in")
class UserController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val userMapRoleService: UserMapRoleService,
    private val roleService: RoleService,
) {

    @PostMapping("/user/register")
    fun registerUser(
        @RequestBody registerUserDto: RegisterUserDto,
        request: HttpServletRequest
    ): ResponseEntity<ToolTrackerResponseDto> {
        registerUserDto.validate(null)

        val userEntity = userService.registerUserByPassword(registerUserDto, null)

        userMapRoleService.create(PatchUserMapRoleDto(userEntity.id, roleService.findRoleById(1).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't find default user role") }.id), userEntity)

        val authToken = UsernamePasswordAuthenticationToken(registerUserDto.email, registerUserDto.password, userMapRoleService.getRoles(userEntity.id))
        val authentication = authenticationManager.authenticate(authToken)
        SecurityContextHolder.getContext().authentication = authentication

        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        )

        return ResponseEntity.ok(ToolTrackerResponseDto(true, ZonedDateTime.now(), HttpStatus.OK, "Registered ${registerUserDto.email}"))
    }

    @PostMapping("/user/login")
    fun login(
        @RequestBody loginDto: LoginDto,
        request: HttpServletRequest,
    ): ResponseEntity<ToolTrackerResponseDto> {

        val userEntity = userService.validateLogin(loginDto)

        val authToken = UsernamePasswordAuthenticationToken(loginDto.email, loginDto.password, userMapRoleService.getRoles(userEntity.id))
        val authentication = authenticationManager.authenticate(authToken)
        SecurityContextHolder.getContext().authentication = authentication

        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        )
        return ResponseEntity.ok(ToolTrackerResponseDto(true, ZonedDateTime.now(), HttpStatus.OK,"Logged in"))
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getUser(
        @PathVariable id: Int
    ): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.findById(id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find toolType with id ($id)") }.toUserDto())
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getUsers(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam name: String?,
        @RequestParam email: String?
    ): ResponseEntity<ToolTrackerPageDto<UserDto>> {
        return ResponseEntity.ok(userService.findUsers(pageable, name, email))
    }
}