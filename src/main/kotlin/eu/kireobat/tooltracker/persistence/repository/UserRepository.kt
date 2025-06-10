package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<UserEntity, Int> {
    fun findByEmail(email: String): Optional<UserEntity>
}