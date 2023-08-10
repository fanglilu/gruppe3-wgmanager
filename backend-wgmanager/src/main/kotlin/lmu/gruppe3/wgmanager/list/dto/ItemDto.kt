package lmu.gruppe3.wgmanager.list.dto

import java.util.*

data class ItemDto(
    var id: UUID,
    var name: String,
    var description: String,
    var listID: UUID,
    var listName: String,
    var isBought: Boolean,
    var owner: String
)
