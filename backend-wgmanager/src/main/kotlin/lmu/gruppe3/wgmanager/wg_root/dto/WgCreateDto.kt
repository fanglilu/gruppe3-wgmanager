package lmu.gruppe3.wgmanager.wg_root.dto

import lmu.gruppe3.wgmanager.feature.dto.CreateFeatureDto

data class WgCreateDto(
    var name: String,
    var features: MutableSet<CreateFeatureDto>
)
