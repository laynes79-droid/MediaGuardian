package com.media.guardian.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.media.guardian.ui.composables.VideoPlayer
import com.media.guardian.viewmodel.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    viewModel: MediaViewModel,
    mediaItemId: Long?,
    navController: NavController
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var newTag by remember { mutableStateOf("") }

    if (mediaItemId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Media not found.")
        }
        return
    }

    val mediaItem = viewModel.getMediaItemById(mediaItemId)
    if (mediaItem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Media not found in ViewModel.")
        }
        return
    }

    val tags by viewModel.getTagsForMediaItem(mediaItemId).collectAsState(initial = null)

    val openWithIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(mediaItem.uri, mediaItem.mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooserIntent = Intent.createChooser(openWithIntent, "Open with")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mediaItem.displayName, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Open with...") },
                            onClick = {
                                context.startActivity(chooserIntent)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate") },
                            onClick = {
                                viewModel.duplicateMedia(mediaItem.uri)
                                Toast.makeText(context, "Duplicating...", Toast.LENGTH_SHORT).show()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    mediaItem.mimeType.startsWith("image/") -> {
                        AsyncImage(
                            model = mediaItem.uri,
                            contentDescription = "Full screen image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    mediaItem.mimeType.startsWith("video/") || mediaItem.mimeType.startsWith("audio/") -> {
                        VideoPlayer(videoUri = mediaItem.uri, modifier = Modifier.fillMaxSize())
                    }
                    else -> Text("Unsupported media type: ${mediaItem.mimeType}")
                }
            }

            // Tags section
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tags", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    tags?.tags?.forEach { tag ->
                        SuggestionChip(
                            onClick = { /* This chip is for display, removal is via the icon */ },
                            label = { Text(tag.name) },
                            modifier = Modifier.padding(end = 8.dp),
                            icon = {
                                IconButton(
                                    onClick = { viewModel.removeTagFromMediaItem(mediaItemId, tag.name) },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove tag")
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = newTag,
                        onValueChange = { newTag = it },
                        label = { Text("Add a new tag") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newTag.isNotBlank()) {
                                viewModel.addTagToMediaItem(mediaItem, newTag.trim())
                                newTag = ""
                            }
                        },
                        enabled = newTag.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add tag")
                    }
                }
            }
        }
    }
}
