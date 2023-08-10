package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty


data class ShoppingListEntry(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("creator") var creator: String,
    @JsonProperty("value") var value: Double,
    @JsonProperty("numItems") var numItems: Int,
    @JsonProperty("private") var private: Boolean,
)
