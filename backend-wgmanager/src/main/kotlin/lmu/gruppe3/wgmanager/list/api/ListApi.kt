package lmu.gruppe3.wgmanager.list.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.list.dto.*
import lmu.gruppe3.wgmanager.list.service.ListService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

//To receive all the different list events from the clients

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/list")
class ListApi(private val listService: ListService) {

    // Returns all the lists associated with the provided WG
    @PostMapping("/getMyLists")
    fun getMyLists(@RequestBody WgRequesting: GetListDto): ResponseEntity<List<ListDto>> {
        val result = this.listService.findBywgID(WgRequesting)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Creates a new list in the database
    @PostMapping("/newList")
    fun createNewList(@RequestBody listRequest: RegisterListDto): ResponseEntity<List<ListDto>> {
        val result = this.listService.createList(listRequest)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Deletes the provided list in the database
    @DeleteMapping("/delete/{id}")
    fun deleteList(@PathVariable id: UUID) {
        this.listService.deleteList(id)
    }

}
