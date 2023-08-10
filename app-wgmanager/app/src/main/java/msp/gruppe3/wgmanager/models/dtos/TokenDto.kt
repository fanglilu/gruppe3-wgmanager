package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenDto(@JsonProperty("token") var token: String)
