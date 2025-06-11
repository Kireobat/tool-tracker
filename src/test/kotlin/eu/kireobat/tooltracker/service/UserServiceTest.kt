package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.TestContainerConfiguration
import eu.kireobat.tooltracker.TestDataLoaderUtil
import eu.kireobat.tooltracker.api.dto.inbound.LoginDto
import eu.kireobat.tooltracker.api.dto.inbound.RegisterUserDto
import eu.kireobat.tooltracker.persistence.entity.RoleEntity
import eu.kireobat.tooltracker.persistence.repository.RoleRepository
import eu.kireobat.tooltracker.persistence.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class UserServiceTest : TestContainerConfiguration() {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var defaultRole: RoleEntity

    @Autowired
    private lateinit var dataSource: DataSource

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
        TestDataLoaderUtil().cleanAllTestData(dataSource)
        TestDataLoaderUtil().insertUsers(dataSource)
        TestDataLoaderUtil().insertRoles(dataSource)
        TestDataLoaderUtil().insertUsersMapRoles(dataSource)
        TestDataLoaderUtil().syncSequences(dataSource)

        defaultRole = roleRepository.findById(1).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find expected default role") }
    }

    @Test
    fun `should register user successfully`() {
        val registerDto = RegisterUserDto(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        val result = userService.registerUserByPassword(registerDto, null)

        assertNotNull(result)
        assertEquals("Test User", result.name)
        assertEquals("test@example.com", result.email)
        assertTrue(passwordEncoder.matches("password123", result.passwordHash))

        // Verify user exists in database
        val savedUser = userRepository.findByEmail("test@example.com")
        assertTrue(savedUser.isPresent)
    }

    @Test
    fun `should throw exception when registering duplicate email`() {
        val registerDto = RegisterUserDto(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        // Register first user
        userService.registerUserByPassword(registerDto, null)

        // Try to register with same email
        val exception = assertThrows<ResponseStatusException> {
            userService.registerUserByPassword(registerDto, null)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertTrue(exception.reason!!.contains("email is already registered"))
    }

    @Test
    fun `should find user by email`() {
        val registerDto = RegisterUserDto(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        userService.registerUserByPassword(registerDto, null)

        val result = userService.findByEmail("test@example.com")

        assertTrue(result.isPresent)
        assertEquals("test@example.com", result.get().email)
    }

    @Test
    fun `should validate login successfully`() {
        val registerDto = RegisterUserDto(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        userService.registerUserByPassword(registerDto, null)

        val loginDto = LoginDto(
            email = "test@example.com",
            password = "password123"
        )

        val result = userService.validateLogin(loginDto)

        assertNotNull(result)
        assertEquals("test@example.com", result.email)
    }

    @Test
    fun `should throw exception for invalid login credentials`() {
        val registerDto = RegisterUserDto(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        userService.registerUserByPassword(registerDto, null)

        val loginDto = LoginDto(
            email = "test@example.com",
            password = "wrongpassword"
        )

        val exception = assertThrows<ResponseStatusException> {
            userService.validateLogin(loginDto)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertTrue(exception.reason!!.contains("Invalid email or password"))
    }

    @Test
    fun `should find user by id`() {
        val registerDto = RegisterUserDto(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        val user = userService.registerUserByPassword(registerDto, null)

        val result = userService.findById(user.id)

        assertTrue(result.isPresent)
        assertEquals(user.id, result.get().id)
    }
}