package lmu.gruppe3.wgmanager.list.domain

import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.list.dto.ItemDto
import org.springframework.data.jpa.repository.Query
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "items")
data class Item(
    @Column(unique = true)
    var name: String,
    var description: String,
    var listID: UUID,
    var isBought: Boolean,
    var owner: UUID,

    )
    : BaseEntity() {

    fun toDto(listName: String, ownersName: String): ItemDto {
        return ItemDto(
            id = this.id,
            name = this.name,
            description = this.description,
            listID = this.listID,
            listName = listName,
            isBought = this.isBought,
            owner = ownersName
        )
    }
}
