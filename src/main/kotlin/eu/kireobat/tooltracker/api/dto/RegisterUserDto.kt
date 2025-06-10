package eu.kireobat.tooltracker.api.dto

data class RegisterUserDto(
    val name: String,
    val email: String,
    val password: String,
)