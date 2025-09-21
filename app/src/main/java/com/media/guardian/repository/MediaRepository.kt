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

    suspend fun removeTagFromMediaItem(mediaItemId: Long, tagName: String) {
        withContext(Dispatchers.IO) {
            tagDao.deleteMediaItemTagCrossRef(
                MediaItemTagCrossRef(mediaItemId = mediaItemId, tagName = tagName)
            )
        }
    }

    suspend fun deleteFiles(uris: List<android.net.Uri>): android.app.PendingIntent? {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.createDeleteRequest(context.contentResolver, uris)
            } else {
                // For older versions, delete directly
                // This requires WRITE_EXTERNAL_STORAGE permission for API < 29
                var filesDeleted = 0
                for (uri in uris) {
                    try {
                        val rowsDeleted = context.contentResolver.delete(uri, null, null)
                        if (rowsDeleted > 0) {
                            filesDeleted++
                        }
                    } catch (e: Exception) {
                        // Handle exceptions, e.g., SecurityException
                    }
                }
                null // Indicate that deletion was handled directly
            }
        }
    }

    suspend fun getMediaFolders(): List<String> {
        val folders = mutableSetOf<String>()
        val projection = arrayOf(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
        val collection = MediaStore.Files.getContentUri("external")

        withContext(Dispatchers.IO) {
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val folderName = cursor.getString(bucketColumn)
                    if (!folderName.isNullOrBlank()) {
                        folders.add(folderName)
                    }
                }
            }
        }
        return folders.sorted()
    }

    suspend fun copyMedia(mediaItem: MediaItem, destinationFolder: String): Boolean {
        return withContext(Dispatchers.IO) {
            val collection = when {
                mediaItem.mimeType.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                mediaItem.mimeType.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                mediaItem.mimeType.startsWith("audio/") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> return@withContext false
            }

            val newMediaDetails = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, mediaItem.displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mediaItem.mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val relativePath = getRelativePathForMimeType(mediaItem.mimeType) + "/" + destinationFolder
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                // For older versions, the system will decide where to save it, less ideal
            }

            val newMediaUri = context.contentResolver.insert(collection, newMediaDetails) ?: return@withContext false

            try {
                context.contentResolver.openInputStream(mediaItem.uri)?.use { inputStream ->
                    context.contentResolver.openOutputStream(newMediaUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    newMediaDetails.clear()
                    newMediaDetails.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(newMediaUri, newMediaDetails, null, null)
                }
                true // Return true on success
            } catch (e: Exception) {
                context.contentResolver.delete(newMediaUri, null, null) // Clean up failed copy
                false // Return false on failure
            }
        }
    }

    suspend fun moveMedia(mediaItem: MediaItem, destinationFolder: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (copyMedia(mediaItem, destinationFolder)) {
                try {
                    context.contentResolver.delete(mediaItem.uri, null, null)
                    true
                } catch (e: Exception) {
                    // If delete fails, we have a copy but the original still exists.
                    // This is not ideal, but it's better than losing the file.
                    false
                }
            } else {
                false
            }
        }
    }

    private fun getRelativePathForMimeType(mimeType: String): String {
        return when {
            mimeType.startsWith("image/") -> android.os.Environment.DIRECTORY_PICTURES
            mimeType.startsWith("video/") -> android.os.Environment.DIRECTORY_MOVIES
            mimeType.startsWith("audio/") -> android.os.Environment.DIRECTORY_MUSIC
            else -> android.os.Environment.DIRECTORY_DOCUMENTS // Fallback
        }
    }
}
