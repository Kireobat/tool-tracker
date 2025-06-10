package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.api.dto.RegisterUserDto
import eu.kireobat.tooltracker.persistence.entity.UserEntity
import eu.kireobat.tooltracker.persistence.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    // endre denne
    fun registerUser(registerUserDto: RegisterUserDto): UserEntity {

        val userEntity = UserEntity(
            name = registerUserDto.name,
            email = registerUserDto.email,
        )

        return userRepository.save(userEntity)
    }
}