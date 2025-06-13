package eu.kireobat.tooltracker.api.exception

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import eu.kireobat.tooltracker.api.dto.outbound.ToolTrackerResponseDto
import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime


@ControllerAdvice
class GlobalControllerAdvice {

    private val logger: Logger = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)

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

    @ExceptionHandler(HttpMessageNotReadableException::class)
fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ToolTrackerResponseDto> {
    val cause = ex.cause
    when {
        cause is InvalidFormatException && cause.targetType == ToolStatusEnum::class.java -> {
            val response = ToolTrackerResponseDto(
                false, 
                ZonedDateTime.now(), 
                HttpStatus.BAD_REQUEST, 
                "Invalid tool status (${cause.value}). Status must be one of: ${ToolStatusEnum.entries.joinToString(", ")}"
            )
            return ResponseEntity.badRequest().body(response)
        }
        cause is InvalidFormatException -> {
            val response = ToolTrackerResponseDto(
                false, 
                ZonedDateTime.now(), 
                HttpStatus.BAD_REQUEST, 
                "Invalid value for field '${cause.message}': ${cause.value}"
            )
            return ResponseEntity.badRequest().body(response)
        }
        else -> {
            val response = ToolTrackerResponseDto(
                false, 
                ZonedDateTime.now(), 
                HttpStatus.BAD_REQUEST, 
                "Invalid request format. Unable to parse request body: ${ex.message}"
            )
            return ResponseEntity.badRequest().body(response)
        }
    }
}

@ExceptionHandler(JsonProcessingException::class)
fun handleJsonProcessingException(ex: JsonProcessingException): ResponseEntity<ToolTrackerResponseDto> {
    logger.error("JSON processing error", ex)
    val response = ToolTrackerResponseDto(
        false, 
        ZonedDateTime.now(), 
        HttpStatus.BAD_REQUEST, 
        "Invalid JSON format: ${ex.message}"
    )
    return ResponseEntity.badRequest().body(response)
}

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ToolTrackerResponseDto> {
        logger.error("Unhandled exception", e)
        val response = e.message?.let {
            ToolTrackerResponseDto(
                false, ZonedDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, "Server error"
            )
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}