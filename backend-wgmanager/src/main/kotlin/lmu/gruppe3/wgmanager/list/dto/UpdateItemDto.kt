package lmu.gruppe3.wgmanager.list.dto

import java.util.*

data class UpdateItemDto(
    var id: UUID,
    var name: String,
    var description: String,
    var bought: String,
)