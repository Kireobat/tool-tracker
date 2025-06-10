package eu.kireobat.tooltracker.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="tool_types")
data class ToolTypeEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "toolTypesSeq")
    @SequenceGenerator(name = "toolTypesSeq", sequenceName = "tool_types_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @Column(name="name")
    var name: String = "",
    @ManyToOne
    @JoinColumn(name="created_by")
    var createdBy: UserEntity,
    @Column(name="created_time")
    var createdTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="modified_by")
    var modifiedBy: UserEntity,
    @Column(name="modified_time")
    var modifiedTime: ZonedDateTime = ZonedDateTime.now(),
)