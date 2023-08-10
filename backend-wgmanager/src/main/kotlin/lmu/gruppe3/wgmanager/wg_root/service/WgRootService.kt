package lmu.gruppe3.wgmanager.wg_root.service

import lmu.gruppe3.wgmanager.common.dto.VoidRequestResponse
import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.common.util.Generators.Companion.generateRandomString
import lmu.gruppe3.wgmanager.feature.domain.Feature
import lmu.gruppe3.wgmanager.feature.repository.FeatureRepository
import lmu.gruppe3.wgmanager.user.enum.UserRole
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import lmu.gruppe3.wgmanager.wg_root.domain.Wg
import lmu.gruppe3.wgmanager.wg_root.dto.InvitationCodeDto
import lmu.gruppe3.wgmanager.wg_root.dto.WgCreateDto
import lmu.gruppe3.wgmanager.wg_root.dto.WgDto
import lmu.gruppe3.wgmanager.wg_root.repository.WgRootRepository
import lmu.gruppe3.wgmanager.wg_user.domain.WgUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.time.LocalDateTime
import java.util.*

const val INVITATION_CODE_LENGTH = 6
const val INVITATION_CODE_EXPIRES_IN_DAYS = 7

@Service
class WgRootService(
    private val wgRootRepository: WgRootRepository,
    private val featureRepository: FeatureRepository,
    private val userRepository: UserRepository
) {
    var logger: Logger = LoggerFactory.getLogger(WgRootService::class.java)


    fun getAll(): List<WgDto> {
        return this.wgRootRepository.findAll().map(Wg::toDto)
    }

    fun getAllWgIds(): List<UUID> {
        return this.wgRootRepository.findAllWgIds()
    }

    fun findWgById(id: UUID): WgDto {
        this.logger.info("Wg  find ID: $id")
        val result = wgRootRepository.findById(id).map(Wg::toDto) ?: throw InvalidParameterException("Wg not found")
        val wgDto: WgDto = result.get()
        if (wgDto.features.isEmpty()) {
            wgDto.features = this.featureRepository.findByWgId(result.get().id).map { it.toDto() }
        }

        return wgDto
    }

    fun createWg(wgCreatDto: WgCreateDto): WgDto {
        if (wgCreatDto.features.isEmpty()) {
            throw InvalidParameterException("Provide at least one feature")
        }
        if (wgCreatDto.name.isEmpty()) {
            throw InvalidParameterException("Provide a name")
        }
        // create a new WG
        val newWg = Wg(
            name = wgCreatDto.name,
            invitationCode = generateRandomString(INVITATION_CODE_LENGTH),
            invitationCodeExpires = LocalDateTime.now().plusDays(INVITATION_CODE_EXPIRES_IN_DAYS.toLong())
        )

        // add creator to User List
        val currentUserId = AuthUtil.getCurrentUserId()
        val userToAdd = this.userRepository.getReferenceById(currentUserId)
        val wgUser = WgUser(userToAdd, newWg, UserRole.ADMIN, LocalDateTime.now())
        newWg.wgUserList.add(wgUser)
        // save the WG
        val savedWg = this.wgRootRepository.save(newWg)
        this.logger.info("Wg  was created. ID: ${newWg.id}")

        // get the features from the WG DTO, sent as CreateFeatureDto
        val featureList: List<Feature> = wgCreatDto.features.map {
            Feature(
                name = it.name,
                wg = savedWg
            )
        }
        //save the features
        this.featureRepository.saveAllAndFlush(featureList)


        // add the features to the corresponding WG response body
        val result = this.wgRootRepository.findById(savedWg.id).get()
        val wgDto: WgDto = result.toDto()
        wgDto.features = this.featureRepository.findByWgId(result.id).map { it.toDto() }
        return wgDto
    }

    fun getInvitationCode(wgId: UUID): InvitationCodeDto {
        this.logger.info("Wg get invitation code from ID: $wgId")
        val wg = wgRootRepository.findById(wgId).get()

        val today = LocalDateTime.now()
        today.minusDays(1) // Make sure the invitation code is at least 1 day valid
        if (today.isBefore(wg.invitationCodeExpires)) {
            return InvitationCodeDto(wg.invitationCode)
        }

        // Create new invitation code
        val invitationCodeExpires = LocalDateTime.now().plusDays(INVITATION_CODE_EXPIRES_IN_DAYS.toLong())
        val invitationCode = generateRandomString(INVITATION_CODE_LENGTH)
        wg.invitationCodeExpires = invitationCodeExpires
        wg.invitationCode = invitationCode

        this.wgRootRepository.saveAndFlush(wg)

        return InvitationCodeDto(invitationCode)
    }

    fun joinWg(invitationCode: String): WgDto {
        this.logger.info("Wg  find invitation code: $invitationCode")
        val wg =
            wgRootRepository.findByInvitationCode(invitationCode) ?: throw InvalidParameterException("Wg not found")

        val today = LocalDateTime.now()
        if (today.isAfter(wg.invitationCodeExpires)) {
            throw InvalidParameterException("Invitation code expired")
        }

        val currentUserId = AuthUtil.getCurrentUserId()
        val userToAdd = this.userRepository.getReferenceById(currentUserId)
        val wgUser = WgUser(userToAdd, wg, UserRole.USER, LocalDateTime.now())
        wg.wgUserList.add(wgUser)

        val savedWg = this.wgRootRepository.saveAndFlush(wg)
        return savedWg.toDto()
    }

    fun leaveWg(id: UUID): VoidRequestResponse {
        this.logger.info("Wg leave: $id")

        var wg = wgRootRepository.findById(id).get()
        val currentUserId = AuthUtil.getCurrentUserId()
        val wgUser = wg.wgUserList.find { it.user.id == currentUserId }
        wg.wgUserList.remove(wgUser)
        val savedWg = this.wgRootRepository.saveAndFlush(wg)
        return VoidRequestResponse(success = true)
    }

    fun deleteWg(id: UUID): VoidRequestResponse {
        this.logger.info("Wg delete: $id")

        var wg = wgRootRepository.findById(id).get()
        val currentUserId = AuthUtil.getCurrentUserId()
        val wgUser = wg.wgUserList.find { it.user.id == currentUserId } ?: throw IllegalAccessError("Not authorized")
        if (wgUser.role != UserRole.ADMIN) {
            throw IllegalAccessError("Not authorized")
        }
        wgRootRepository.deleteById(wg.id)
        return VoidRequestResponse(success = true)
    }
}