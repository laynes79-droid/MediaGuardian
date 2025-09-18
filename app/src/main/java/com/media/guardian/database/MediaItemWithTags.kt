package com.media.guardian.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.media.guardian.data.MediaItem

data class MediaItemWithTags(
    @Embedded val mediaItem: MediaItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "name",
        associateBy = Junction(MediaItemTagCrossRef::class)
    )
    val tags: List<Tag>
)
