package eu.kireobat.tooltracker.api.dto.inbound

import io.swagger.v3.oas.annotations.media.Schema

data class PatchUserMapRoleDto(
    @Schema(description = "ID of the user to be mapped to a role", example = "1")
    val userId: Int,
    @Schema(description = "ID of the role to be assigned to the user", example = "2")
    val roleId: Int
)
