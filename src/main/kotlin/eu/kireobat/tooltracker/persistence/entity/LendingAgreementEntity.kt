package eu.kireobat.tooltracker.persistence.entity

import eu.kireobat.tooltracker.api.dto.outbound.LendingAgreementDto
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="lending_agreements")
data class LendingAgreementEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lendingAgreementsSeq")
    @SequenceGenerator(name = "lendingAgreementsSeq", sequenceName = "lending_agreements_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @ManyToOne
    @JoinColumn(name="tool_id")
    var tool: ToolEntity,
    @ManyToOne
    @JoinColumn(name="borrower_id")
    var borrower: UserEntity,
    @Column(name="lending_start_time")
    var lendingStartTime: ZonedDateTime,
    @Column(name="expected_return_time")
    var expectedReturnTime: ZonedDateTime,
    @Column(name="return_time")
    var returnTime: ZonedDateTime? = null,
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

fun LendingAgreementEntity.toLendingAgreementDto() = LendingAgreementDto(id, tool.toToolDto(), borrower.toUserDto(), lendingStartTime, expectedReturnTime, returnTime, createdBy.toUserDto(), createdTime, modifiedBy?.toUserDto(), modifiedTime)