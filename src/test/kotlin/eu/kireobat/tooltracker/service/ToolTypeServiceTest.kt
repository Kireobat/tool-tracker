package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.persistence.entity.ToolTypeEntity
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.RoleRepository
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
class ToolTypeServiceTest : TestContainerConfiguration() {

    @Autowired
    private lateinit var toolTypeService: ToolTypeService

    @Autowired
    private lateinit var toolTypeRepository: ToolTypeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var testUser: UserEntity

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
        TestDataLoaderUtil().cleanAllTestData(dataSource)
        TestDataLoaderUtil().insertUsers(dataSource)
        TestDataLoaderUtil().insertRoles(dataSource)
        TestDataLoaderUtil().insertUsersMapRoles(dataSource)
        TestDataLoaderUtil().syncSequences(dataSource)

        // Create test role
        val defaultRole = roleRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected default role") }

        // Create test user
        testUser = userRepository.findById(2).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected test user") }

        // Set authentication context
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
    fun `should create tool type successfully`() {
        val result = toolTypeService.create("Hammer")

        assertNotNull(result)
        assertEquals("Hammer", result.name)
        assertEquals(testUser.id, result.createdBy.id)

        // Verify it's saved in database
        val saved = toolTypeRepository.findById(result.id)
        assertTrue(saved.isPresent)
        assertEquals("Hammer", saved.get().name)
    }

    @Test
    fun `should find tool type by id`() {
        val toolType = toolTypeRepository.save(ToolTypeEntity(
            name = "Screwdriver",
            createdBy = testUser
        ))

        val result = toolTypeService.findById(toolType.id)

        assertTrue(result.isPresent)
        assertEquals(toolType.id, result.get().id)
        assertEquals("Screwdriver", result.get().name)
    }

    @Test
    fun `should find tool types with name filter`() {
        // Create test tool types
        toolTypeRepository.save(ToolTypeEntity(name = "Electric Drill", createdBy = testUser))
        toolTypeRepository.save(ToolTypeEntity(name = "Manual Drill", createdBy = testUser))
        toolTypeRepository.save(ToolTypeEntity(name = "Hammer", createdBy = testUser))

        val pageable = PageRequest.of(0, 10)

        // Test with name filter
        val result = toolTypeService.findToolTypes(pageable, "Drill")

        assertEquals(2, result.totalItems)
        assertTrue(result.page.all { it.name.contains("Drill") })
    }

    @Test
    fun `should find all tool types without filter`() {
        // Create test tool types
        val toolTypes = listOf("Hammer", "Screwdriver", "Drill")
        toolTypes.forEach { name ->
            toolTypeRepository.save(ToolTypeEntity(name = name, createdBy = testUser))
        }

        val pageable = PageRequest.of(0, 10)
        val result = toolTypeService.findToolTypes(pageable, null)

        assertEquals(3, result.totalItems)
        assertEquals(toolTypes.sorted(), result.page.map { it.name }.sorted())
    }
}