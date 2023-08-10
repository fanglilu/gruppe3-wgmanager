package lmu.gruppe3.wgmanager.list.repository

import lmu.gruppe3.wgmanager.list.domain.Item
import lmu.gruppe3.wgmanager.list.domain.Shopping_List
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ItemRepository : JpaRepository<Item, UUID> {

    fun findByListID(listId: UUID): List<Item>?
}