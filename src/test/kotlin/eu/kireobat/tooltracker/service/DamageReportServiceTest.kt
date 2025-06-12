package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.api.dto.inbound.CreateDamageReportDto
import eu.kireobat.tooltracker.persistence.entity.DamageReportEntity
import eu.kireobat.tooltracker.persistence.entity.LendingAgreementEntity
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class DamageReportServiceTest : TestContainerConfiguration() {

    @Autowired
    private lateinit var damageReportService: DamageReportService

    @Autowired
    private lateinit var damageReportRepository: DamageReportRepository

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
        val defaultRole = roleRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find expected default role") }

        // Create test user
        testUser = userRepository.findById(2).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find expected test user") }

        testTool = toolRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find expected test tool") }

        // Create test lending agreement
        testLendingAgreement = lendingAgreementRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find expected test lendingAgreement") }

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
    fun `should create damage report with tool ID`() {
        val createDto = CreateDamageReportDto(
            toolId = testTool.id,
            lendingAgreementId = null,
            description = "Hammer head is loose"
        )

        val result = damageReportService.create(createDto)

        assertNotNull(result)
        assertEquals(testTool.id, result.tool?.id)
        assertEquals("Hammer head is loose", result.description)
        assertEquals(testUser.id, result.createdBy.id)
    }

    @Test
    fun `should create damage report with lending agreement ID`() {
        val createDto = CreateDamageReportDto(
            toolId = null,
            lendingAgreementId = testLendingAgreement.id,
            description = "Tool damaged during lending"
        )

        val result = damageReportService.create(createDto)

        assertNotNull(result)
        assertEquals(testLendingAgreement.id, result.lendingAgreement?.id)
        assertEquals("Tool damaged during lending", result.description)
        assertEquals(testUser.id, result.createdBy.id)
    }

    @Test
    fun `should throw exception when both tool ID and lending agreement ID are null`() {
        val createDto = CreateDamageReportDto(
            toolId = null,
            lendingAgreementId = null,
            description = "Some damage"
        )

        val exception = assertThrows<ResponseStatusException> {
            damageReportService.create(createDto)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertTrue(exception.reason!!.contains("lendingAgreementId and toolId can not be null at the same time"))
    }

    @Test
    fun `should find damage report by id`() {
        val report = damageReportRepository.save(DamageReportEntity(
            tool = testTool,
            lendingAgreement = null,
            description = "Test damage",
            createdBy = testUser
        ))

        val result = damageReportService.findById(report.id)

        assertTrue(result.isPresent)
        assertEquals(report.id, result.get().id)
        assertEquals("Test damage", result.get().description)
    }

    @Test
    fun `should find reports with filters`() {
        // Create test reports
        val report1 = damageReportRepository.save(DamageReportEntity(
            tool = testTool,
            lendingAgreement = null,
            description = "Tool damage",
            createdBy = testUser
        ))

        val report2 = damageReportRepository.save(DamageReportEntity(
            tool = null,
            lendingAgreement = testLendingAgreement,
            description = "Agreement damage",
            createdBy = testUser
        ))

        val pageable = PageRequest.of(0, 10)

        // Test filter by tool ID
        val resultByTool = damageReportService.findReports(
            pageable = pageable,
            lendingAgreementId = null,
            toolId = testTool.id
        )

        assertEquals(1, resultByTool.totalItems)
        assertEquals(testTool.id, resultByTool.page[0].tool?.id)

        // Test filter by lending agreement ID
        val resultByAgreement = damageReportService.findReports(
            pageable = pageable,
            lendingAgreementId = testLendingAgreement.id,
            toolId = null
        )

        assertEquals(1, resultByAgreement.totalItems)
        assertEquals(testLendingAgreement.id, resultByAgreement.page[0].lendingAgreement?.id)
    }
}