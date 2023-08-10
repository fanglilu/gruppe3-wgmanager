package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WgDuplicate(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("invitationCode") var invitationCode: String,
    @JsonProperty("invitationCodeExpires") var invitationCodeExpires: String,
    @JsonProperty("created") var createdDate: String,
    @JsonProperty("updated") var updated: String,

    )
