package eu.kireobat.tooltracker.persistence.entity

import eu.kireobat.tooltracker.api.dto.outbound.ToolServiceEventDto
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="tool_services") // <-- rename til tool_service_events
data class ToolServiceEventEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "toolServicesSeq")
    @SequenceGenerator(name = "toolServicesSeq", sequenceName = "tool_services_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @ManyToOne
    @JoinColumn(name = "damage_report_id")
    var damageReport: DamageReportEntity,
    @Column(name="service_start_time")
    var serviceStartTime: ZonedDateTime? = ZonedDateTime.now(),
    @Column(name="service_stop_time")
    var serviceStopTime: ZonedDateTime? = null,
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

fun ToolServiceEventEntity.toToolServiceEventDto() = ToolServiceEventDto(id, damageReport.toDamageReportDto(),serviceStartTime!!,serviceStopTime,createdTime,createdBy.id,modifiedTime,modifiedBy?.id)