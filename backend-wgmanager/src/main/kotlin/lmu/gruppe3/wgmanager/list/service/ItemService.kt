package lmu.gruppe3.wgmanager.list.service

import lmu.gruppe3.wgmanager.list.domain.Item
import lmu.gruppe3.wgmanager.list.dto.DeleteItemDto
import lmu.gruppe3.wgmanager.list.dto.ItemDto
import lmu.gruppe3.wgmanager.list.dto.RegisterItemDto
import lmu.gruppe3.wgmanager.list.dto.UpdateItemDto
import lmu.gruppe3.wgmanager.list.repository.ItemRepository
import lmu.gruppe3.wgmanager.list.repository.ListRepository
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.util.*

@Service
class ItemService(private val itemRepository: ItemRepository, private val listRepository: ListRepository, private val userRepository: UserRepository) {

    var logger: Logger = LoggerFactory.getLogger(ItemService::class.java)

    //Returns the requested item as a DTO
    fun findItemById(ID: UUID): ItemDto {
        val item = this.itemRepository.findById(ID).orElseThrow { InvalidParameterException("Could not find Item with id = $ID") }
        return item.toDto(this.listRepository.findById(item.listID).get().name, this.userRepository.findById(item.owner).get().name)
    }

    // Creates a new item using the provided DTO and saves it into the database
    fun createItem(itemDto: RegisterItemDto): List<ItemDto> {
        val newItem = Item(
            name = itemDto.name,
            description = itemDto.description,
            listID = itemDto.listID,
            owner = itemDto.owner,
            isBought = itemDto.isBought,
        )

        var list = this.listRepository.findById(itemDto.listID)
        list.get().numItems += 1
        this.listRepository.saveAndFlush(list.get())

        this.itemRepository.saveAndFlush(newItem)
        this.logger.info("item ${newItem.id} created")

        val itemDtos: MutableList<ItemDto> = mutableListOf()
        val items = this.itemRepository.findByListID(itemDto.listID)?: throw InvalidParameterException("Item not found")
        for (item in items){
            // This is searching for the creators name using the saved UUID
            itemDtos += item.toDto(this.listRepository.findById(item.listID).get().name, this.userRepository.findById(item.owner).get().name)
        }
        return itemDtos
    }

    // Finds all items on a list
    fun findItemsOnList(listID: UUID): List<ItemDto>?{
        val itemDtos: MutableList<ItemDto> = mutableListOf()
        val items = this.itemRepository.findByListID(listID)?: throw InvalidParameterException("Item not found")
        for (item in items){
            // This is searching for the creators name using the saved UUID
            itemDtos += item.toDto(this.listRepository.findById(item.listID).get().name, this.userRepository.findById(item.owner).get().name)
        }
        return itemDtos
    }

    // Finds all items which are on the provided lists
    fun findMultipleItemsOnList(listUUIDs: List<UUID>): List<ItemDto>?{
        val itemDtos: MutableList<ItemDto> = mutableListOf()
        for (listID in listUUIDs) {
            val items = this.itemRepository.findByListID(listID)
            if (items != null) {
                for (item in items) {
                    // This is searching for the creators name using the saved UUID
                    itemDtos += item.toDto(this.listRepository.findById(item.listID).get().name, this.userRepository.findById(item.owner).get().name)
                }
            }
        }
        return itemDtos
    }

    // Updates an update according to the possible changes from the send DTO
    fun updateItem(item: UpdateItemDto): List<ItemDto>{
        var dbItem = this.itemRepository.findById(item.id)

        // These are the fields which can be changed in the client
        dbItem.get().name = item.name
        dbItem.get().description = item.description
        dbItem.get().isBought = item.bought.toBoolean()

        // Saves the changed item to the database
        val savedItem = this.itemRepository.saveAndFlush(dbItem.get())
        this.logger.info("item ${savedItem.id} updated")

        val itemDtos: MutableList<ItemDto> = mutableListOf()
        val items = this.itemRepository.findByListID(dbItem.get().listID)?: throw InvalidParameterException("Item not found")
        for (item in items){
            // This is searching for the creators name using the saved UUID
            itemDtos += item.toDto(this.listRepository.findById(item.listID).get().name, this.userRepository.findById(item.owner).get().name)
        }
        return itemDtos
    }

    // Deletes the provided item from the database
    fun deleteItem(itemToDelete: DeleteItemDto): Boolean? {

        // Update the number of items on the list from which the item is deleted
        var list = this.listRepository.findById(itemToDelete.listId)
        list.get().numItems -= 1

        // Push the changes to the database
        this.listRepository.saveAndFlush(list.get())
        this.itemRepository.deleteById(itemToDelete.itemId)
        return true
    }

}
