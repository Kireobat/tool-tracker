package eu.kireobat.tooltracker.api.dto.inbound

import io.swagger.v3.oas.annotations.media.Schema

data class LoginDto (
    @Schema(description = "Email address of the user", example = "john.fortnite@eg.store")
    val email: String,
    @Schema(description = "Password for the user account", example = "password123")
    val password: String
)