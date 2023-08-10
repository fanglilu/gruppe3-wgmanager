package lmu.gruppe3.wgmanager.list.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.list.dto.*
import lmu.gruppe3.wgmanager.list.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

//To receive all the different events from the clients

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/item")
class ItemApi(private val itemService: ItemService) {

    // Registers a new item in the servers database
    @PostMapping("/newItem")
    fun createNewItem(@RequestBody itemRequest: RegisterItemDto): ResponseEntity<List<ItemDto>> {
        val result = this.itemService.createItem(itemRequest)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Returns all items which are on the provided list
    @GetMapping("/getItem/{id}")
    fun getItemsFromList(@PathVariable id: UUID): ResponseEntity<List<ItemDto>> {
        val result = this.itemService.findItemsOnList(id)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Returns all items on all lists provided - for the shopping mode
    @PostMapping("/getMultipleItems")
    fun getMultipleItemsFromList(@RequestBody listUUIDs: List<UUID>): ResponseEntity<List<ItemDto>> {
        val result = this.itemService.findMultipleItemsOnList(listUUIDs)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Saves an update done to the provided item (change the name etc.) in the database
    @PostMapping("/updateItem")
    fun getItemsFromList(@RequestBody updatedItem: UpdateItemDto): ResponseEntity<List<ItemDto>> {
        val result = this.itemService.updateItem(updatedItem)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Deletes the provided item from the database
    @PostMapping("/deleteItem")
    fun getItemsFromList(@RequestBody itemToDelete: DeleteItemDto): ResponseEntity<Boolean> {
        val result = this.itemService.deleteItem(itemToDelete)
        return ResponseEntity(result, HttpStatus.OK)
    }
}
