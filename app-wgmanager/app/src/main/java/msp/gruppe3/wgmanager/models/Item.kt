package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Item(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("name") var name: String,
    @JsonProperty("description") var description: String,
    @JsonProperty("listID") var listID: UUID,
    @JsonProperty("listName") var listName: String,
    @JsonProperty("isBought") var isBought: Boolean,
    @JsonProperty("owner") var owner: String,
)
