package lmu.gruppe3.wgmanager.notifications.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.common.dto.MessageDto
import lmu.gruppe3.wgmanager.notifications.dto.CreateNotificationDto
import lmu.gruppe3.wgmanager.notifications.dto.NotificationDto
import lmu.gruppe3.wgmanager.notifications.service.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/notification")
class NotificationAPI(private val notificationService: NotificationService) {

    @GetMapping("/getNotifications/{requester}")
    fun getNotifications(@PathVariable requester: UUID): ResponseEntity<List<NotificationDto>> {
        val result = this.notificationService.getNotifications(requester)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/newNotification")
    fun storeNewNotification(@RequestBody createNotificationDto: CreateNotificationDto) {
        this.notificationService.createNotification(createNotificationDto)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteNotification(@PathVariable id: UUID): ResponseEntity<MessageDto> {
        this.notificationService.deleteNotification(id)
        return ResponseEntity(MessageDto("Success"), HttpStatus.OK)
    }
}
