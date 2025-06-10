package eu.kireobat.tooltracker.api.exception

import eu.kireobat.tooltracker.api.dto.ToolTrackerResponseDto
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@ControllerAdvice
class GlobalControllerAdvice {

    private val logger: Logger = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ToolTrackerResponseDto> {
        logger.error(e.message)
        val response = e.message?.let {
            ToolTrackerResponseDto(
                false, ZonedDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, it
            )
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ToolTrackerResponseDto> {
        val response = e.message?.let {
            ToolTrackerResponseDto(
                false, ZonedDateTime.now(), HttpStatus.BAD_REQUEST, it
            )
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<ToolTrackerResponseDto> {
        logger.warn(e.message)
        val response = e.body.detail?.let {
            ToolTrackerResponseDto(
                false, ZonedDateTime.now(), HttpStatus.valueOf(e.body.status), it
            )
        }
        return ResponseEntity.status(HttpStatus.valueOf(e.body.status)).body(response)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<ToolTrackerResponseDto> {
        logger.info(e.message)
        val response = e.message?.let {
            ToolTrackerResponseDto(
                false, ZonedDateTime.now(), HttpStatus.UNAUTHORIZED, it
            )
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }
}