package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.api.dto.inbound.RegisterToolDto
import eu.kireobat.tooltracker.common.enums.ToolStatusEnum
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.entity.toToolTypeDto
import eu.kireobat.tooltracker.persistence.repository.RoleRepository
import eu.kireobat.tooltracker.persistence.repository.ToolRepository
import eu.kireobat.tooltracker.persistence.repository.ToolTypeRepository
import eu.kireobat.tooltracker.persistence.repository.UserRepository
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
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class ToolServiceTest : TestContainerConfiguration() {

    @Autowired
    private lateinit var toolService: ToolService

    @Autowired
    private lateinit var toolRepository: ToolRepository

    @Autowired
    private lateinit var toolTypeRepository: ToolTypeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var testUser: UserEntity
    private lateinit var testToolType: ToolTypeEntity

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
        TestDataLoaderUtil().cleanAllTestData(dataSource)
        TestDataLoaderUtil().insertUsers(dataSource)
        TestDataLoaderUtil().insertRoles(dataSource)
        TestDataLoaderUtil().insertUsersMapRoles(dataSource)
        TestDataLoaderUtil().insertToolTypes(dataSource)
        TestDataLoaderUtil().syncSequences(dataSource)

        // Create test role
        val defaultRole = roleRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find expected default role") }

        // Create test user
        testUser = userRepository.findById(2).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find expected test user") }

        // Create test tool type
        testToolType = toolTypeRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find expected test toolType") }

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
    fun `should register tool successfully`() {
        val registerDto = RegisterToolDto(
            name = "Test Hammer",
            serial = "TH001",
            type = testToolType.id
        )

        val result = toolService.registerTool(registerDto)

        assertNotNull(result)
        assertEquals("Test Hammer", result.name)
        assertEquals("TH001", result.serial)
        assertEquals(ToolStatusEnum.AVAILABLE, result.status)
        assertEquals(testToolType.id, result.type.id)
        assertEquals(testUser.id, result.createdBy.id)
    }

    @Test
    fun `should find tool by id`() {
        val tool = toolRepository.save(ToolEntity(
            name = "Test Tool",
            serial = "TT001",
            status = ToolStatusEnum.AVAILABLE,
            type = testToolType,
            createdBy = testUser
        ))

        val result = toolService.findById(tool.id)

        assertTrue(result.isPresent)
        assertEquals(tool.id, result.get().id)
        assertEquals("Test Tool", result.get().name)
    }

    @Test
    fun `should find tools with filters`() {
        // Create test tools
        val tool1 = toolRepository.save(ToolEntity(
            name = "Electric Drill",
            serial = "ED001",
            status = ToolStatusEnum.AVAILABLE,
            type = testToolType,
            createdBy = testUser
        ))

        val tool2 = toolRepository.save(ToolEntity(
            name = "Manual Drill",
            serial = "MD001",
            status = ToolStatusEnum.UNAVAILABLE,
            type = testToolType,
            createdBy = testUser
        ))

        val pageable = PageRequest.of(0, 10)

        // Test name filter
        val resultByName = toolService.findTools(
            pageable = pageable,
            name = "Electric",
            serial = null,
            toolTypeId = null,
            status = null
        )

        assertEquals(1, resultByName.totalItems)
        assertEquals("Electric Drill", resultByName.page[0].name)

        // Test status filter
        val resultByStatus = toolService.findTools(
            pageable = pageable,
            name = null,
            serial = null,
            toolTypeId = null,
            status = ToolStatusEnum.AVAILABLE
        )

        assertEquals(1, resultByStatus.totalItems)
        assertEquals(ToolStatusEnum.AVAILABLE, resultByStatus.page[0].status)
    }

    @Test
    fun `should find all tools without filters`() {
        // Create multiple test tools
        repeat(3) { index ->
            toolRepository.save(ToolEntity(
                name = "Tool $index",
                serial = "T00$index",
                status = ToolStatusEnum.AVAILABLE,
                type = testToolType,
                createdBy = testUser
            ))
        }

        val pageable = PageRequest.of(0, 10)
        val result = toolService.findTools(
            pageable = pageable
        )

        assertEquals(3, result.totalItems)
        assertTrue(result.page.all { it.type == testToolType.toToolTypeDto() })
    }
}