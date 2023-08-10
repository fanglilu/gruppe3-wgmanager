package lmu.gruppe3.wgmanager.feature.service

import FeatureDto
import lmu.gruppe3.wgmanager.feature.domain.Feature
import lmu.gruppe3.wgmanager.feature.repository.FeatureRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.util.*

@Service
class FeatureService(
    private val featureRepository: FeatureRepository
) {
    var logger: Logger = LoggerFactory.getLogger(FeatureService::class.java)


    fun getAll(): List<FeatureDto> {
        return this.featureRepository.findAll().map(Feature::toDto)
    }

    fun getFeatureById(id: UUID): Optional<FeatureDto> {
        this.logger.info("Wg  find ID: $id")
        return featureRepository.findById(id).map(Feature::toDto) ?: throw InvalidParameterException("Wg not found")
    }

    fun getFeaturesByWgId(wgId: UUID): List<FeatureDto> {
        return this.featureRepository.findByWgId(wgId).map { it.toDto() }
    }

}