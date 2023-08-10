package lmu.gruppe3.wgmanager.list.dto

import java.util.*

data class ListDto(
    var id: UUID,
    var name: String,
    var creator: String,
    var value: Double,
    var numItems: Int,
    var private: Boolean
)
