package msp.gruppe3.wgmanager.models.dtos

import java.util.*

data class UpdateItemDto(
    var id: UUID,
    var name: String,
    var description: String,
    var bought: String,
)