package eu.kireobat.tooltracker.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="users")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersSeq")
    @SequenceGenerator(name = "usersSeq", sequenceName = "users_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @Column(name="name")
    var name: String = "",
    @Column(name="email")
    var email: String = "",
    @Column(name="password_hash")
    var passwordHash: String = "",
    @Column(name="created_by")
    var createdBy: Int = 0,
    @Column(name="created_time")
    var createdTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="modified_by")
    var modifiedBy: Int = 0,
    @Column(name="modified_time")
    var modifiedTime: ZonedDateTime = ZonedDateTime.now(),
)