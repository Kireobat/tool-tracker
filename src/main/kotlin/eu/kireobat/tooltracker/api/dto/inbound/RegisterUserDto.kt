package eu.kireobat.tooltracker.api.dto.inbound

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class RegisterUserDto(
    val name: String,
    val email: String,
    val password: String,
)

fun RegisterUserDto.validate(index: Number?) {

    val errorList = mutableListOf<String>()

    val emailRegex: Regex = "^(\\w|\\d|\\.)+@(\\w|\\d)+\\.\\w+\$".toRegex()

    if (name.isBlank()) {
        errorList.add("name must not be blank")
    } else if (name.length > 100) {
        errorList.add("username is too long (max 100 characters)")
    }

    if (password.isBlank()) {
        errorList.add("password must not be blank")
    } else if (password.length < 8) {
        errorList.add("password must have at least 8 characters")
    }

    if (email.isBlank()) {
        errorList.add("email must not be blank")
    } else if (email.length > 255) {
        errorList.add("email is too long (max 255 characters)")
    } else if (!email.matches(emailRegex)) {
        errorList.add("email is not valid")
    }

    val errorMessage = if (index != null) {
        "index is $index" + errorList.joinToString(", ")
    } else {
        errorList.joinToString(", ")
    }

    if (errorList.isNotEmpty()) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST,errorMessage)
    }
}