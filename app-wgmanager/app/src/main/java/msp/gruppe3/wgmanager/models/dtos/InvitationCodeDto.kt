package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class InvitationCodeDto(@JsonProperty("invitationCode") var invitationCode: String)
