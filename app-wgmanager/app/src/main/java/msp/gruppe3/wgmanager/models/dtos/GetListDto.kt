package msp.gruppe3.wgmanager.models.dtos
import java.util.*

data class GetListDto(
    var wgID: UUID,
    var requester: UUID,
)