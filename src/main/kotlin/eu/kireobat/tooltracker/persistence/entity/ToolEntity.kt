package eu.kireobat.tooltracker.persistence.entity

import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="tools")
data class ToolEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "toolsSeq")
    @SequenceGenerator(name = "toolsSeq", sequenceName = "tools_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @Column(name="name")
    var name: String = "",
    @Column(name="serial")
    var serial: String = "",
    @Column(name="status")
    var status: ToolStatusEnum,
    @ManyToOne
    @JoinColumn(name="type")
    var type: ToolTypeEntity,
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