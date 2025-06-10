package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.RoleEntity
import eu.kireobat.tooltracker.persistence.entity.ToolEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ToolRepository: JpaRepository<ToolEntity, Int> {
}