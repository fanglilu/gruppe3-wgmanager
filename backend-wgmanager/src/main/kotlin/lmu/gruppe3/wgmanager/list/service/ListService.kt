package lmu.gruppe3.wgmanager.list.service

import lmu.gruppe3.wgmanager.list.domain.Item
import lmu.gruppe3.wgmanager.list.domain.Shopping_List
import lmu.gruppe3.wgmanager.list.dto.*
import lmu.gruppe3.wgmanager.list.repository.ItemRepository
import lmu.gruppe3.wgmanager.list.repository.ListRepository
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.util.*

@Service
class ListService(private val listRepository: ListRepository, private val itemRepository: ItemRepository, private val userRepository: UserRepository) {

    var logger: Logger = LoggerFactory.getLogger(ListService::class.java)

    // Returns the lists which are associated with the provided WG
    fun findBywgID(getLists: GetListDto): List<ListDto> {
        val listDTOs: MutableList<ListDto> = mutableListOf()
        val lists = this.listRepository.findBywgID(getLists.wgID) ?: throw InvalidParameterException("list not found")
        for (list in lists){
            // This is searching for the creators name using the saved UUID
            if ((!list.private) or (list.creator == getLists.requester)) {
                listDTOs += list.toDto(this.userRepository.findById(list.creator).get().name)
            }
        }
        return listDTOs
    }

    // Creates a list using the provided DTO
    fun createList(listDto: RegisterListDto): List<ListDto> {

        val newList = Shopping_List(
            name = listDto.name,
            wgID = listDto.wgID,
            creator = listDto.creator,
            value = listDto.value,
            numItems = listDto.numItems,
            private = listDto.private,
        )

        // Push the new list to the database
        this.listRepository.saveAndFlush(newList)
        this.logger.info("list ${newList.id} created")

        val listDTOs: MutableList<ListDto> = mutableListOf()
        val lists = this.listRepository.findBywgID(listDto.wgID) ?: throw InvalidParameterException("list not found")
        for (list in lists){
            // This is searching for the creators name using the saved UUID
            if ((!list.private) or (list.creator == listDto.creator)) {
                listDTOs += list.toDto(this.userRepository.findById(list.creator).get().name)
            }
        }
        return listDTOs
    }

    // Deletes the list and all items on said list in the database
    fun deleteList(listToDelete: UUID): Boolean {
        // Go through all items on the list and delete them in the database
        val itemsToDelete = this.itemRepository.findByListID(listToDelete)?.map { it.id }
        if (itemsToDelete != null) {
            this.itemRepository.deleteAllById(itemsToDelete)
        }

        this.listRepository.deleteById(listToDelete)
        return true
    }

}