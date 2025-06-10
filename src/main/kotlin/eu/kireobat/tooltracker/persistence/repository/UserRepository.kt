package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, Int> {
}