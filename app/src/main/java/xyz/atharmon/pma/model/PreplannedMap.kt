package xyz.atharmon.pma.model

import androidx.annotation.DrawableRes

data class PreplannedMap(
    @DrawableRes val imageResourceId: Int,
    val title: String,
    val description: String
)