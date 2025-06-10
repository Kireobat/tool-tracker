package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.LoginDto
import eu.kireobat.tooltracker.api.dto.RegisterUserDto
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.entity.UserMapRoleEntity
import eu.kireobat.tooltracker.persistence.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapRoleService: UserMapRoleService,
    private val roleService: RoleService,
) {

    fun registerUserByPassword(registerUserDto: RegisterUserDto, createdBy: UserEntity?): UserEntity {

        val errorList = mutableListOf<String>()

        if (userRepository.findByEmail(registerUserDto.email).isPresent) {
            errorList.add("email is already registered")
        }

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }

        val hashedPassword = passwordEncoder.encode(registerUserDto.password)

        val tempSave = userRepository.saveAndFlush(UserEntity(
            name = registerUserDto.name,
            passwordHash = hashedPassword,
            email = registerUserDto.email,
        ))
        if (createdBy == null) {
            tempSave.createdBy = tempSave.id
        } else {
            tempSave.createdBy = createdBy.id
        }

        val userEntity = userRepository.saveAndFlush(tempSave)

        val userMapRoleEntity = UserMapRoleEntity(
            user = userEntity,
            role = roleService.getRoleById(1).getOrElse { throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Default user role not found") },
            createdBy = userEntity,
        )

        userMapRoleService.create(userMapRoleEntity)

        return userEntity
    }

    fun findByEmail(email: String): Optional<UserEntity> {
        return userRepository.findByEmail(email)
    }

    fun validateLogin(loginDto: LoginDto): UserEntity {
        val storedUser = userRepository.findByEmail(loginDto.email).getOrElse {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password")
        }

        val match = passwordEncoder.matches(loginDto.password, storedUser.passwordHash)
        if (!match) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password")
        }

        return storedUser
    }
}