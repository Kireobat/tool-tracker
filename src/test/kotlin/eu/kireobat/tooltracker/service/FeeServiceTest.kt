package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.api.dto.inbound.CreateLendingAgreementDto
import eu.kireobat.tooltracker.api.dto.inbound.PatchLendingAgreementDto
import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.LendingAgreementRepository
import eu.kireobat.tooltracker.persistence.repository.RoleRepository
import eu.kireobat.tooltracker.persistence.repository.ToolRepository
import eu.kireobat.tooltracker.persistence.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
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

@SpringBootTest
@Transactional
class FeeServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var lendingAgreementService: LendingAgreementService

    @Autowired
    private lateinit var feeService: FeeService

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
    fun `should create not find any offending agreements`() {

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

        assertEquals(0,offendingAgreements.size)
    }

    @Test
    fun `should create and find offending agreements`() {

        val lendingAgreementEntity = lendingAgreementService.create(
            CreateLendingAgreementDto(
                toolId = testTool.id,
                borrowerId = borrowerUser.id,
                lendingStartTime = ZonedDateTime.now().minusDays(8),
                expectedReturnTime = ZonedDateTime.now().minusDays(7)
            )
        )

        val offendingAgreements = feeService.findOffendingLendingAgreements()

        assertEquals(1,offendingAgreements.size)
        assertEquals(lendingAgreementEntity.id,offendingAgreements[0].id)
    }
}