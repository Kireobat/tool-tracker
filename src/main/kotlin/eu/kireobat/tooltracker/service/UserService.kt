package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.inbound.LoginDto
import eu.kireobat.tooltracker.api.dto.inbound.RegisterUserDto
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
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

        return userRepository.saveAndFlush(tempSave)
    }

    fun findByEmail(email: String): Optional<UserEntity> {
        return userRepository.findByEmail(email)
    }

    fun findById(id: Int): Optional<UserEntity> {
        return userRepository.findById(id)
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

    fun findByAuthentication(): UserEntity {

        return when (val principal = SecurityContextHolder.getContext().authentication.principal) {
            // email/pass users must have email
            is CustomUserDetails -> userRepository.findByEmail(principal.username).orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found") }
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to parse authentication. (Not local or github)")
        }
    }

    fun hasAuthentication(): Boolean {

        return when (val principal = SecurityContextHolder.getContext().authentication.principal) {
            // email/pass users must have email
            is CustomUserDetails -> true
            else -> false
        }
    }
}