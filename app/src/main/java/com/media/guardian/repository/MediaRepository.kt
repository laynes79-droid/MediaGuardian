package com.media.guardian.repository

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.media.guardian.data.MediaItem
import com.media.guardian.database.Tag
import com.media.guardian.database.TagDao
import com.media.guardian.database.MediaItemTagCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MediaRepository(
    val context: Context,
    private val tagDao: TagDao
) {

    suspend fun getImages(tag: String? = null): List<MediaItem> {
        return queryMedia(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, tag)
    }

    suspend fun getVideos(tag: String? = null): List<MediaItem> {
        return queryMedia(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, tag)
    }

    suspend fun getAudios(tag: String? = null): List<MediaItem> {
        return queryMedia(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, tag)
    }

    private suspend fun queryMedia(collection: android.net.Uri, tag: String?): List<MediaItem> {
        val mediaList = mutableListOf<MediaItem>()

        val itemIds = tag?.let { tagDao.getMediaItemIdsForTag(it) }
        if (tag != null && itemIds.isNullOrEmpty()) {
            return emptyList() // No items with this tag, so return early
        }

        val selection = itemIds?.let { "${MediaStore.MediaColumns._ID} IN (${it.joinToString(",")})" }

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.MIME_TYPE
        )
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        withContext(Dispatchers.IO) {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    val contentUri = ContentUris.withAppendedId(collection, id)

                    mediaList.add(
                        MediaItem(
                            id = id,
                            uri = contentUri,
                            displayName = displayName,
                            size = size,
                            dateAdded = dateAdded,
                            mimeType = mimeType
                        )
                    )
                }
            }
        }
        return mediaList
    }

    suspend fun duplicateMedia(uri: android.net.Uri) {
        withContext(Dispatchers.IO) {
            val mimeType = context.contentResolver.getType(uri)
            val collection = when {
                mimeType?.startsWith("image/") == true -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                mimeType?.startsWith("video/") == true -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                mimeType?.startsWith("audio/") == true -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> null
            } ?: return@withContext // Unsupported type

            var originalDisplayName: String? = null
            context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    originalDisplayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                }
            }

            if (originalDisplayName == null) return@withContext

            val newDisplayName = "copy_of_${originalDisplayName}"

            val newMediaDetails = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, newDisplayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val newMediaUri = context.contentResolver.insert(collection, newMediaDetails) ?: return@withContext

            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    context.contentResolver.openOutputStream(newMediaUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    newMediaDetails.clear()
                    newMediaDetails.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(newMediaUri, newMediaDetails, null, null)
                }
            } catch (e: Exception) {
                context.contentResolver.delete(newMediaUri, null, null)
            }
        }
    }

    // Tag related methods
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    fun getTagsForMediaItem(mediaItemId: Long) = tagDao.getTagsForMediaItem(mediaItemId)

    suspend fun addTagToMediaItem(mediaItem: MediaItem, tagName: String) {
        withContext(Dispatchers.IO) {
            // First, ensure the MediaItem is in the database
            tagDao.insertMediaItem(mediaItem)
            // Then, ensure the Tag is in the database
            tagDao.insertTag(Tag(name = tagName))
            // Finally, create the relationship
            tagDao.insertMediaItemTagCrossRef(MediaItemTagCrossRef(mediaItemId = mediaItem.id, tagName = tagName))
        }
    }

    fun getMediaItemsWithTag(tagName: String) = tagDao.getMediaItemsWithTag(tagName)
}
