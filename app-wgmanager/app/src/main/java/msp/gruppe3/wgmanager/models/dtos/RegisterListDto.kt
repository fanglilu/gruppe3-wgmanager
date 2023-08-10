package msp.gruppe3.wgmanager.models.dtos
import java.util.*

data class RegisterListDto(
    var name: String,
    var wgID: UUID,
    var creator: UUID,
    var value: Double,
    var numItems: Int,
    var private: Boolean
)