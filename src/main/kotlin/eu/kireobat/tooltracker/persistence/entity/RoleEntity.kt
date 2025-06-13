package eu.kireobat.tooltracker.persistence.entity

import eu.kireobat.tooltracker.api.dto.outbound.RoleDto
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="roles")
data class RoleEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolesSeq")
    @SequenceGenerator(name = "rolesSeq", sequenceName = "roles_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @Column(name="name")
    var name: String = "",
    @Column(name="description")
    var description: String = "",
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

fun RoleEntity.toRoleDto() = RoleDto(id, name, description, createdTime, createdBy.id, modifiedTime, modifiedBy?.id)