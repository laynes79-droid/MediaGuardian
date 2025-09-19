package com.media.guardian.database

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["mediaItemId", "tagName"],
    indices = [Index(value = ["tagName"])]
)
data class MediaItemTagCrossRef(
    val mediaItemId: Long,
    val tagName: String
)
