package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.enums.Features
import java.util.*

data class Feature(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("name") var name: Features,
    @JsonProperty("wg") var wg: WgDuplicate
)
