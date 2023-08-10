package msp.gruppe3.wgmanager.models.dtos

import msp.gruppe3.wgmanager.enums.Features

/**
 * Data class for MyFeatureSelectAdapter to have them in the recycler view
 *
 * @author Marcello Alte
 */
data class FeatureDto(
    var name: Features,
)
