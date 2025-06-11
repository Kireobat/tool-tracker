package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.*
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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class LendingAgreementServiceTest : TestContainerConfiguration() {

    @Autowired
    private lateinit var lendingAgreementService: LendingAgreementService

    @Autowired
    private lateinit var lendingAgreementRepository: LendingAgreementRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var toolRepository: ToolRepository

    @Autowired
    private lateinit var toolTypeRepository: ToolTypeRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var testUser: UserEntity
    private lateinit var borrowerUser: UserEntity
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

        // Create test role
        val defaultRole = roleRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected default role") }

        // Create test user
        testUser = userRepository.findById(2).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test user") }
        borrowerUser = userRepository.findById(3).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected borrow user") }

        testTool = toolRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test tool") }

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
    fun `should create lending agreement successfully`() {
        val createDto = CreateLendingAgreementDto(
            borrowerId = borrowerUser.id,
            toolId = testTool.id,
            lendingStartTime = ZonedDateTime.now(),
            expectedReturnTime = ZonedDateTime.now().plusDays(7)
        )

        val result = lendingAgreementService.create(createDto)

        assertNotNull(result)
        assertEquals(borrowerUser.id, result.borrower.id)
        assertEquals(testTool.id, result.tool.id)
        assertEquals(testUser.id, result.createdBy.id)
        assertNotNull(result.lendingStartTime)
    }

    @Test
    fun `should find lending agreement by id`() {
        val agreement = lendingAgreementRepository.save(LendingAgreementEntity(
            borrower = borrowerUser,
            tool = testTool,
            createdBy = testUser
        ))

        val result = lendingAgreementService.findById(agreement.id)

        assertTrue(result.isPresent)
        assertEquals(agreement.id, result.get().id)
        assertEquals(borrowerUser.id, result.get().borrower.id)
    }

    @Test
    fun `should find agreements with filters`() {
        // Create test agreements
        val agreement1 = lendingAgreementRepository.save(LendingAgreementEntity(
            borrower = borrowerUser,
            tool = testTool,
            createdBy = testUser
        ))

        // Create another tool and agreement
        val toolType2 = toolTypeRepository.save(ToolTypeEntity(
            name = "Screwdriver",
            createdBy = testUser
        ))

        val tool2 = toolRepository.save(ToolEntity(
            name = "Test Screwdriver",
            serial = "TS001",
            status = ToolStatusEnum.AVAILABLE,
            type = toolType2,
            createdBy = testUser
        ))

        val agreement2 = lendingAgreementRepository.save(LendingAgreementEntity(
            borrower = borrowerUser,
            tool = tool2,
            createdBy = testUser
        ))

        val pageable = PageRequest.of(0, 10)

        // Test filter by tool ID
        val resultByTool = lendingAgreementService.findAgreements(
            pageable = pageable,
            toolId = testTool.id,
            borrowerId = null,
            lentAfter = null,
            lentBefore = null
        )

        assertEquals(1, resultByTool.totalItems)
        assertEquals(testTool.id, resultByTool.page[0].tool.id)

        // Test filter by borrower ID
        val resultByBorrower = lendingAgreementService.findAgreements(
            pageable = pageable,
            toolId = null,
            borrowerId = borrowerUser.id,
            lentAfter = null,
            lentBefore = null
        )

        assertEquals(2, resultByBorrower.totalItems)
        assertTrue(resultByBorrower.page.all { it.borrower.id == borrowerUser.id })
    }
}