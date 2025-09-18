package com.media.guardian.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.media.guardian.R
import com.media.guardian.data.MediaItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaGridItem(
    mediaItem: MediaItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val placeholder = if (mediaItem.mimeType.startsWith("audio/")) {
        painterResource(id = R.drawable.ic_music_note)
    } else {
        null
    }

    Box(
        modifier = modifier
            .padding(1.dp)
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mediaItem.uri)
                    .crossfade(true)
                    .build(),
                placeholder = placeholder,
                error = placeholder,
                contentDescription = mediaItem.displayName,
                modifier = Modifier.aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = mediaItem.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                // Checkmark icon can be added here
            }
        }
    }
}
