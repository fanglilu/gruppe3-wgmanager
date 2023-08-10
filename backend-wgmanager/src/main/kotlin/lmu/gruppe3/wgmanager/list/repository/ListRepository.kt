package lmu.gruppe3.wgmanager.list.repository

import lmu.gruppe3.wgmanager.list.domain.Shopping_List
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ListRepository : JpaRepository<Shopping_List, UUID> {

    fun findBywgID(wgID: UUID): List<Shopping_List>?
}