package com.media.guardian.database

import androidx.room.Entity

@Entity(primaryKeys = ["mediaItemId", "tagName"])
data class MediaItemTagCrossRef(
    val mediaItemId: Long,
    val tagName: String
)
