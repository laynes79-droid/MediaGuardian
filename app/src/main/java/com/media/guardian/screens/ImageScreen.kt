package com.media.guardian.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.media.guardian.data.MediaItem
import com.media.guardian.data.ViewMode
import com.media.guardian.ui.composables.MediaGridItem
import com.media.guardian.ui.composables.MediaListItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageScreen(
    mediaItems: List<MediaItem>,
    selectedIds: Set<Long>,
    viewMode: ViewMode,
    onMediaClick: (MediaItem) -> Unit,
    onMediaLongClick: (MediaItem) -> Unit
) {
    when (viewMode) {
        ViewMode.LIST_DETAILS -> {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(mediaItems, key = { it.id }) { mediaItem ->
                    MediaListItem(
                        mediaItem = mediaItem,
                        onClick = { onMediaClick(mediaItem) },
                        onLongClick = { onMediaLongClick(mediaItem) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
        else -> {
            val minSize = if (viewMode == ViewMode.GRID_NORMAL) 128.dp else 180.dp
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = minSize),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(mediaItems, key = { it.id }) { mediaItem ->
                    MediaGridItem(
                        mediaItem = mediaItem,
                        isSelected = mediaItem.id in selectedIds,
                        onClick = { onMediaClick(mediaItem) },
                        onLongClick = { onMediaLongClick(mediaItem) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}
