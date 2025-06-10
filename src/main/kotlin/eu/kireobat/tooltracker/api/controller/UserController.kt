package eu.kireobat.tooltracker.api.controller

import eu.kireobat.tooltracker.api.dto.RegisterUserDto
import eu.kireobat.tooltracker.api.dto.ToolTrackerResponseDto
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.service.UserService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Bad Request", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ToolTrackerResponseDto::class))])
@Tag(name = "User endpoints", description = "Endpoints related to registering and logging in")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/user/register")
    fun registerTool(@RequestBody registerUserDto: RegisterUserDto): UserEntity {
        return userService.registerUser(registerUserDto)
    }
}