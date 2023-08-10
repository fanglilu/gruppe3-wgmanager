package lmu.gruppe3.wgmanager.wg_user.domain

import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class WgUserKey(
    @Column(name = "user_id")
    val userId: UUID,
    @Column(name = "wg_id")
    val wgId: UUID
) : Serializable {}

