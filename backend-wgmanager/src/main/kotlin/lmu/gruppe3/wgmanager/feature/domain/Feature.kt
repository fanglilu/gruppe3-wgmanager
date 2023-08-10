package lmu.gruppe3.wgmanager.feature.domain

import FeatureDto
import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.feature.enums.Features
import lmu.gruppe3.wgmanager.wg_root.domain.Wg
import javax.persistence.*


@Entity
@Table(name = "FEATURE")
data class Feature(

    var name: Features,

    @ManyToOne
    @JoinColumn(name = "wg_id", nullable = false)
    val wg: Wg

) : BaseEntity() {

    fun toDto(): FeatureDto {
        return FeatureDto(
            id = this.id,
            name = this.name,
            wg = this.wg

        )
    }
}