package eu.kireobat.tooltracker.scheduled

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.api.dto.inbound.CreateFeeDto
import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_LATE_FEE_AMOUNT_NOK
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_LATE_FEE_REASON
import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.*
import eu.kireobat.tooltracker.service.CustomUserDetails
import eu.kireobat.tooltracker.service.FeeService
import eu.kireobat.tooltracker.service.LendingAgreementService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class TaxCollectorTest : TestContainerConfiguration() {

    @Autowired
    private lateinit var taxCollector: TaxCollector

    @Autowired
    private lateinit var feeService: FeeService

    @Autowired
    private lateinit var lendingAgreementService: LendingAgreementService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var toolRepository: ToolRepository

    @Autowired
    private lateinit var lendingAgreementRepository: LendingAgreementRepository

    @Autowired
    private lateinit var feeRepository: FeeRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var testUser: UserEntity
    private lateinit var borrowerUser: UserEntity
    private lateinit var taxCollectorUser: UserEntity
    private lateinit var testTool: ToolEntity

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
        TestDataLoaderUtil().cleanAllTestData(dataSource)
        TestDataLoaderUtil().insertUsers(dataSource)
        TestDataLoaderUtil().insertRoles(dataSource)
        TestDataLoaderUtil().insertUsersMapRoles(dataSource)
        TestDataLoaderUtil().insertToolTypes(dataSource)
        TestDataLoaderUtil().insertTools(dataSource)
        TestDataLoaderUtil().syncSequences(dataSource)

        val taxCollectorRole = roleRepository.findById(2).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected taxCollector role") }

        testUser = userRepository.findById(2).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test user") }
        borrowerUser = userRepository.findById(3).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected borrower user") }
        taxCollectorUser = userRepository.findById(4).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected tax collector user") }

        testTool = toolRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test tool") }

        // run as tax collector
        val customUserDetails = CustomUserDetails(taxCollectorUser, listOf(SimpleGrantedAuthority(taxCollectorRole.name)))

        // Set authentication context
        val auth = UsernamePasswordAuthenticationToken(
            customUserDetails,
            "password",
            listOf(SimpleGrantedAuthority(taxCollectorRole.name))
        )
        SecurityContextHolder.getContext().authentication = auth
    }

    @Test
    fun `should not create fees when no broken contracts exist`() {
        // Create a valid lending agreement that's not overdue
        val validAgreement = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(1),
                expectedReturnTime = ZonedDateTime.now().plusDays(7)
            )
        )

        val feesBefore = feeRepository.count()

        // Run the tax collector
        taxCollector.checkForBrokenContracts()

        val feesAfter = feeRepository.count()

        assertEquals(feesBefore, feesAfter)
    }

    @Test
    fun `should create fees for overdue contracts without existing fees`() {
        // Create an overdue lending agreement
        val overdueAgreement = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(10),
                expectedReturnTime = ZonedDateTime.now().minusDays(3)
            )
        )

        val feesBefore = feeRepository.count()

        // Run the tax collector
        taxCollector.checkForBrokenContracts()

        val feesAfter = feeRepository.count()
        val createdFees = feeService.findFeesByLendingAgreementId(overdueAgreement.id)

        assertEquals(feesBefore + 1, feesAfter)
        assertEquals(1, createdFees.size)
        assertEquals(DEFAULT_LATE_FEE_REASON, createdFees[0].reason)
        assertEquals(DEFAULT_LATE_FEE_AMOUNT_NOK, createdFees[0].feeAmount)
        assertEquals(FeeStatusEnum.UNPAID, createdFees[0].status)
        assertEquals(taxCollectorUser.id, createdFees[0].createdBy.id)
    }

    @Test
    fun `should not create additional fees for contracts that already have recent fees`() {
        // Create an overdue lending agreement
        val overdueAgreement = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(10),
                expectedReturnTime = ZonedDateTime.now().minusDays(3)
            )
        )

        // Create a recent fee by tax collector
        feeService.createFee(
            CreateFeeDto(
                lendingAgreementId = overdueAgreement.id,
                reason = "Recent fee",
                feeAmount = 100
            ),
            taxCollectorUser
        )

        val feesBefore = feeRepository.count()

        // Run the tax collector
        taxCollector.checkForBrokenContracts()

        val feesAfter = feeRepository.count()

        assertEquals(feesBefore, feesAfter)
    }

    @Test
    fun `should create fees for contracts with old tax collector fees`() {
        // Create an overdue lending agreement
        val overdueAgreement = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(20),
                expectedReturnTime = ZonedDateTime.now().minusDays(10)
            )
        )

        // Create an old fee by tax collector (more than 7 days old)
        val oldFee = feeService.createFee(
            CreateFeeDto(
                lendingAgreementId = overdueAgreement.id,
                reason = "Old fee",
                feeAmount = 100
            ),
            taxCollectorUser
        )

        // Manually set the creation time to be more than 7 days ago
        oldFee.createdTime = ZonedDateTime.now().minusDays(8)
        feeRepository.save(oldFee)

        val feesBefore = feeRepository.count()

        // Run the tax collector
        taxCollector.checkForBrokenContracts()

        val feesAfter = feeRepository.count()
        val allFees = feeService.findFeesByLendingAgreementId(overdueAgreement.id)

        assertEquals(feesBefore + 1, feesAfter)
        assertEquals(2, allFees.size)
        assertTrue(allFees.any { it.reason == DEFAULT_LATE_FEE_REASON })
    }

    @Test
    fun `should only consider fees created by tax collector for timing check`() {
        // Create an overdue lending agreement
        val overdueAgreement = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(10),
                expectedReturnTime = ZonedDateTime.now().minusDays(3)
            )
        )

        // Create a recent fee by a different user (not tax collector)
        feeService.createFee(
            CreateFeeDto(
                lendingAgreementId = overdueAgreement.id,
                reason = "User fee",
                feeAmount = 50
            ),
            testUser
        )

        val feesBefore = feeRepository.count()

        // Run the tax collector
        taxCollector.checkForBrokenContracts()

        val feesAfter = feeRepository.count()
        val allFees = feeService.findFeesByLendingAgreementId(overdueAgreement.id)

        // Should create a new fee because the existing fee was not created by tax collector
        assertEquals(feesBefore + 1, feesAfter)
        assertEquals(2, allFees.size)
        assertTrue(allFees.any { it.createdBy.id == taxCollectorUser.id })
    }
}