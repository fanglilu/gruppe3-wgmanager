package lmu.gruppe3.wgmanager.list.domain

import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.list.dto.ListDto
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "lists")
data class Shopping_List(
    @Column(unique = true)
    var name: String,
    var wgID: UUID,
    var creator: UUID,
    var value: Double,
    var numItems: Int,
    var private: Boolean,


    ) : BaseEntity() {

    fun toDto(creatorName: String): ListDto {
        return ListDto(
            id = this.id,
            name = this.name,
            creator = creatorName,
            value = this.value,
            numItems = this.numItems,
            private = this.private
        )
    }
}
