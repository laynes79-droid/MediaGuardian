package com.media.guardian.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.media.guardian.data.MediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMediaItemTagCrossRef(crossRef: MediaItemTagCrossRef)

    @Delete
    suspend fun deleteMediaItemTagCrossRef(crossRef: MediaItemTagCrossRef)

    @Transaction
    @Query("SELECT * FROM Tag")
    fun getAllTags(): Flow<List<Tag>>

    @Transaction
    @Query("SELECT * FROM MediaItem WHERE id = :mediaItemId")
    fun getTagsForMediaItem(mediaItemId: Long): Flow<MediaItemWithTags>

    @Transaction
    @Query("SELECT * FROM Tag WHERE name = :tagName")
    fun getMediaItemsWithTag(tagName: String): Flow<List<MediaItemWithTags>>

    @Query("SELECT mediaItemId FROM MediaItemTagCrossRef WHERE tagName = :tagName")
    suspend fun getMediaItemIdsForTag(tagName: String): List<Long>

    // We also need to be able to insert MediaItems into the database when they are tagged.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItem)
}
