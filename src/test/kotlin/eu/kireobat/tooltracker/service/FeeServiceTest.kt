package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.api.dto.inbound.CreateFeeDto
import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.inbound.PatchLendingAgreementDto
import eu.kireobat.tooltracker.common.enums.FeeStatusEnum
import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import javax.sql.DataSource
import kotlin.test.assertNotNull

@SpringBootTest
@Transactional
class FeeServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var lendingAgreementService: LendingAgreementService

    @Autowired
    private lateinit var feeService: FeeService

    @Autowired
    private lateinit var feeRepository: FeeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var toolRepository: ToolRepository

    @Autowired
    private lateinit var lendingAgreementRepository: LendingAgreementRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var testUser: UserEntity
    private lateinit var borrowerUser: UserEntity
    private lateinit var testTool: ToolEntity
    private lateinit var testLendingAgreement: LendingAgreementEntity

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
        TestDataLoaderUtil().cleanAllTestData(dataSource)
        TestDataLoaderUtil().insertUsers(dataSource)
        TestDataLoaderUtil().insertRoles(dataSource)
        TestDataLoaderUtil().insertUsersMapRoles(dataSource)
        TestDataLoaderUtil().insertToolTypes(dataSource)
        TestDataLoaderUtil().insertTools(dataSource)
        TestDataLoaderUtil().insertLendingAgreements(dataSource)
        TestDataLoaderUtil().syncSequences(dataSource)

        // Create test role
        val defaultRole = roleRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected default role") }

        // Create test user
        testUser = userRepository.findById(2).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test user") }
        borrowerUser = userRepository.findById(3).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected borrower user") }

        testTool = toolRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test tool") }

        // Create test lending agreement
        testLendingAgreement = lendingAgreementRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test lendingAgreement") }

        val customUserDetails = CustomUserDetails(testUser, listOf(SimpleGrantedAuthority(defaultRole.name)))

        // Set authentication context
        val auth = UsernamePasswordAuthenticationToken(
            customUserDetails,
            "password",
            listOf(SimpleGrantedAuthority(defaultRole.name))
        )
        SecurityContextHolder.getContext().authentication = auth
    }

    @Test
    fun `should create fee successfully`() {
        val createFeeDto = CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "Late return fee",
            feeAmount = 250
        )

        val result = feeService.createFee(createFeeDto)

        assertNotNull(result)
        assertEquals(testLendingAgreement.id, result.lendingAgreement.id)
        assertEquals("Late return fee", result.reason)
        assertEquals(250, result.feeAmount)
        assertEquals(FeeStatusEnum.UNPAID, result.status)
        assertEquals(testUser.id, result.createdBy.id)
    }

    @Test
    fun `should create fee with override user`() {
        val createFeeDto = CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "System generated fee",
            feeAmount = 100
        )

        val result = feeService.createFee(createFeeDto, borrowerUser)

        assertNotNull(result)
        assertEquals(borrowerUser.id, result.createdBy.id)
        assertEquals("System generated fee", result.reason)
    }

    @Test
    fun `should find fee by id`() {
        val createFeeDto = CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "Test fee",
            feeAmount = 150
        )

        val createdFee = feeService.createFee(createFeeDto)
        val foundFee = feeService.findById(createdFee.id)

        assertTrue(foundFee.isPresent)
        assertEquals(createdFee.id, foundFee.get().id)
        assertEquals("Test fee", foundFee.get().reason)
    }

    @Test
    fun `should find fees by lending agreement id`() {
        val createFeeDto1 = CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "First fee",
            feeAmount = 100
        )

        val createFeeDto2 = CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "Second fee",
            feeAmount = 200
        )

        feeService.createFee(createFeeDto1)
        feeService.createFee(createFeeDto2)

        val fees = feeService.findFeesByLendingAgreementId(testLendingAgreement.id)

        assertEquals(2, fees.size)
        assertTrue(fees.any { it.reason == "First fee" })
        assertTrue(fees.any { it.reason == "Second fee" })
    }

    @Test
    fun `should find fees with filters`() {
        // Create another lending agreement for testing
        val anotherAgreement = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now(),
                expectedReturnTime = ZonedDateTime.now().plusDays(7)
            )
        )

        // Create fees with different properties
        val fee1 = feeService.createFee(CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "High fee",
            feeAmount = 500
        ))

        val fee2 = feeService.createFee(CreateFeeDto(
            lendingAgreementId = anotherAgreement.id,
            reason = "Low fee",
            feeAmount = 100
        ))

        // Update one fee status
        fee1.status = FeeStatusEnum.PAID
        feeRepository.save(fee1)

        val pageable = PageRequest.of(0, 10)

        // Test filter by lending agreement ID
        val resultByAgreement = feeService.findFees(
            pageable = pageable,
            lendingAgreementId = testLendingAgreement.id
        )

        assertEquals(1, resultByAgreement.totalItems)
        assertEquals(testLendingAgreement.id, resultByAgreement.page[0].lendingAgreement.id)

        // Test filter by borrower ID
        val resultByBorrower = feeService.findFees(
            pageable = pageable,
            borrowerId = borrowerUser.id
        )

        assertEquals(2, resultByBorrower.totalItems)
        assertTrue(resultByBorrower.page.all { it.lendingAgreement.borrowerId == borrowerUser.id })

        // Test filter by status
        val resultByStatus = feeService.findFees(
            pageable = pageable,
            status = FeeStatusEnum.PAID
        )

        assertEquals(1, resultByStatus.totalItems)
        assertEquals(FeeStatusEnum.PAID, resultByStatus.page[0].status)

        // Test filter by fee amount range
        val resultByAmount = feeService.findFees(
            pageable = pageable,
            feeAmountMin = 200,
            feeAmountMax = 600
        )

        assertEquals(1, resultByAmount.totalItems)
        assertEquals(500, resultByAmount.page[0].feeAmount)
    }

    @Test
    fun `should not find any offending agreements when all are returned on time`() {
        val lendingAgreementEntity = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(8),
                expectedReturnTime = ZonedDateTime.now().minusDays(1)
            )
        )

        lendingAgreementService.patch(
            PatchLendingAgreementDto(id = lendingAgreementEntity.id, returnTime = ZonedDateTime.now().minusDays(3))
        )

        val offendingAgreements = feeService.findOffendingLendingAgreements()

        assertEquals(0, offendingAgreements.size)
    }

    @Test
    fun `should find offending agreements when contracts are overdue`() {
        val lendingAgreementEntity = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(8),
                expectedReturnTime = ZonedDateTime.now().minusDays(7)
            )
        )

        val offendingAgreements = feeService.findOffendingLendingAgreements()

        assertEquals(1, offendingAgreements.size)
        assertEquals(lendingAgreementEntity.id, offendingAgreements[0].id)
    }

    @Test
    fun `should find all fees without filters`() {
        // Create multiple fees
        feeService.createFee(CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "Fee 1",
            feeAmount = 100
        ))

        feeService.createFee(CreateFeeDto(
            lendingAgreementId = testLendingAgreement.id,
            reason = "Fee 2",
            feeAmount = 200
        ))

        val pageable = PageRequest.of(0, 10)
        val result = feeService.findFees(pageable)

        assertEquals(2, result.totalItems)
    }
}