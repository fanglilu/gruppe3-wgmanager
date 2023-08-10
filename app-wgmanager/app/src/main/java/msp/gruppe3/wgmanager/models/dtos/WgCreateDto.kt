package msp.gruppe3.wgmanager.models.dtos

data class WgCreateDto(
    val name: String,
    val features: List<FeatureDto>
)
