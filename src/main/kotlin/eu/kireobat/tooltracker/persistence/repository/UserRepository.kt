package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<UserEntity, Int> {
    fun findByEmail(email: String): Optional<UserEntity>

    @Query("select user from UserEntity user " +
            "where " +
            "(:name is null or lower(cast(user.name as string)) like coalesce(concat('%', lower(cast(:name as string)), '%'), '%')) and " +
            "(:email is null or lower(cast(user.email as string)) like coalesce(concat('%', lower(cast(:email as string)), '%'), '%'))")
    fun findAllWithFilter(
        pageable: Pageable,
        @Param("name") name: String?,
        @Param("email") email: String?
    ): Page<UserEntity>
}