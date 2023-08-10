package lmu.gruppe3.wgmanager.list.dto
import java.util.*

data class GetListDto(
    var wgID: UUID,
    var requester: UUID,
)