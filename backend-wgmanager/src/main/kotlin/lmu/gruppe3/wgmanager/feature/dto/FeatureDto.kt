import lmu.gruppe3.wgmanager.feature.enums.Features
import lmu.gruppe3.wgmanager.wg_root.domain.Wg
import java.util.*

data class FeatureDto(
    var id: UUID,
    var name: Features,
    val wg: Wg


)