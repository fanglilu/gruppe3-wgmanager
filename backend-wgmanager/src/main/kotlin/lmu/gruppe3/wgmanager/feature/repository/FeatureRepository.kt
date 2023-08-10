package lmu.gruppe3.wgmanager.feature.repository

import lmu.gruppe3.wgmanager.feature.domain.Feature
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FeatureRepository : JpaRepository<Feature, UUID> {
    fun findByWgId(wgId: UUID): List<Feature>
}
