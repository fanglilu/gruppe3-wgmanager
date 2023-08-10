package msp.gruppe3.wgmanager.models.dtos

import java.util.*

data class RegisterItemDto(
    var name: String,
    var description: String,
    var listID: String,
    var isBought: Boolean,
    var owner: UUID,
)