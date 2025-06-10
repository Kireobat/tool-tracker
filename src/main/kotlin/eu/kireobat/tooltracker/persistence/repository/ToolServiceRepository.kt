package eu.kireobat.tooltracker.persistence.repository

import eu.kireobat.tooltracker.persistence.entity.RoleEntity
import eu.kireobat.tooltracker.persistence.entity.ToolServiceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ToolServiceRepository: JpaRepository<ToolServiceEntity, Int> {
}