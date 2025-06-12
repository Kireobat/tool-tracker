package eu.kireobat.tooltracker.persistence.entity

import eu.kireobat.tooltracker.api.dto.outbound.FeeDto
import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "fees")
data class FeeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feesSeq")
    @SequenceGenerator(name = "feesSeq", sequenceName = "fees_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Int = 0,
    @ManyToOne
    @JoinColumn(name="lending_agreement_id")
    var lendingAgreement: LendingAgreementEntity,
    @Column(name="reason")
    var reason: String,
    @Column(name="fee_amount")
    var feeAmount: Int,
    @Column(name="status")
    var status: FeeStatusEnum,
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

fun FeeEntity.toFeeDto() = FeeDto(id,lendingAgreement.toLendingAgreementDto(),reason,feeAmount,status,createdTime,createdBy.id,modifiedTime,modifiedBy?.id)
