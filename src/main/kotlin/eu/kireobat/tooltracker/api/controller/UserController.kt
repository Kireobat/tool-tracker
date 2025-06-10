package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.LoginDto
import eu.kireobat.tooltracker.api.dto.RegisterUserDto
import eu.kireobat.tooltracker.api.dto.ToolTrackerResponseDto
import eu.kireobat.tooltracker.api.dto.validate
import eu.kireobat.tooltracker.service.UserMapRoleService
import eu.kireobat.tooltracker.service.UserService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*
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
    private val userMapRoleService: UserMapRoleService
) {

    @PostMapping("/user/register")
    fun registerUser(
        @RequestBody registerUserDto: RegisterUserDto,
        request: HttpServletRequest
    ): ResponseEntity<ToolTrackerResponseDto> {
        registerUserDto.validate(null)

        val userEntity = userService.registerUserByPassword(registerUserDto, null)

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

    @GetMapping("/hasRole")
    @PreAuthorize("hasRole('USER')")
    fun getAdminStatus(): ResponseEntity<Boolean> {
        return ResponseEntity.ok(true)
    }

    @GetMapping("/roles")
    @PreAuthorize("!isAnonymous()")
    fun protected(): List<String> {
        return SecurityContextHolder.getContext().authentication.authorities.map { auth -> auth.authority}
    }

}