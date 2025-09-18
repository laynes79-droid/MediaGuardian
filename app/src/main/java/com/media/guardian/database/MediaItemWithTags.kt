package com.media.guardian.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.media.guardian.data.MediaItem

// This class is not an entity, but a data class to represent the result of a query.
// I need to make MediaItem an entity for this to work.
// I will modify MediaItem to be an entity.
data class MediaItemWithTags(
    @Embedded val mediaItem: MediaItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "name",
        associateBy = Junction(MediaItemTagCrossRef::class)
    )
    val tags: List<Tag>
)
