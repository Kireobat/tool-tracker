package eu.kireobat.tooltracker.persistence.entity

import eu.kireobat.tooltracker.api.dto.outbound.DamageReportDto
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="damage_reports")
data class DamageReportEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "damageReportsSeq")
    @SequenceGenerator(name = "damageReportsSeq", sequenceName = "damage_reports_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @ManyToOne
    @JoinColumn(name = "lending_agreement_id")
    var lendingAgreement: LendingAgreementEntity? = null,
    @ManyToOne
    @JoinColumn(name = "tool_id")
    var tool: ToolEntity? = null,
    @Column(name = "description")
    var description: String,
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

fun DamageReportEntity.toDamageReportDto() = DamageReportDto(id, lendingAgreement?.toLendingAgreementDto(), tool?.toToolDto(), description, createdTime, createdBy.id, modifiedTime, modifiedBy?.id)