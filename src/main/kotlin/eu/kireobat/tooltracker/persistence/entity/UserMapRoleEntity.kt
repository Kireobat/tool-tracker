package eu.kireobat.tooltracker.persistence.entity

import eu.kireobat.tooltracker.api.dto.outbound.UserMapRoleDto
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="users_map_roles")
data class UserMapRoleEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolesSeq")
    @SequenceGenerator(name = "rolesSeq", sequenceName = "roles_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @ManyToOne
    @JoinColumn(name="user_id")
    var user: UserEntity,
    @ManyToOne
    @JoinColumn(name="role_id")
    var role: RoleEntity,
    @ManyToOne
    @JoinColumn(name="created_by")
    var createdBy: UserEntity,
    @Column(name="created_time")
    var createdTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="modified_by")
    var modifiedBy: UserEntity? = null,
    @Column(name="modified_time")
    var modifiedTime: ZonedDateTime? = null,
)

fun UserMapRoleEntity.toUserMapRoleDto() = UserMapRoleDto(id,user.id,role.id,createdBy.id,createdTime,modifiedBy?.id, modifiedTime)