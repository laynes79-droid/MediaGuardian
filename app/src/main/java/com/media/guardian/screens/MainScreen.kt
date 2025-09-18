package com.media.guardian.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.media.guardian.data.MediaItem
import com.media.guardian.data.ViewMode
import com.media.guardian.ui.composables.DrawerContent
import com.media.guardian.viewmodel.MediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: MediaViewModel,
    onMediaClick: (MediaItem) -> Unit
) {
    val images by viewModel.images.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val audios by viewModel.audios.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val isSelectionModeActive by viewModel.isSelectionModeActive.collectAsState(initial = false)
    val viewMode by viewModel.viewMode.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showViewModeMenu by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val tabTitles = listOf("Images", "Videos", "Audio")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    viewModel = viewModel,
                    onSortChanged = { scope.launch { drawerState.close() } },
                    onTagSelected = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (isSelectionModeActive) {
                    ContextualTopAppBar(
                        selectionCount = selectedIds.size,
                        onClose = { viewModel.clearSelection() },
                        onDelete = { viewModel.deleteSelectedFiles() }
                    )
                } else {
                    TopAppBar(
                        title = { Text("Media Guardian") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Open menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showViewModeMenu = true }) {
                                Icon(Icons.Default.ViewList, contentDescription = "Change view mode")
                            }
                            DropdownMenu(
                                expanded = showViewModeMenu,
                                onDismissRequest = { showViewModeMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Normal Grid") },
                                    onClick = {
                                        viewModel.onViewModeChanged(ViewMode.GRID_NORMAL)
                                        showViewModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Large Grid") },
                                    onClick = {
                                        viewModel.onViewModeChanged(ViewMode.GRID_LARGE)
                                        showViewModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Details List") },
                                    onClick = {
                                        viewModel.onViewModeChanged(ViewMode.LIST_DETAILS)
                                        showViewModeMenu = false
                                    }
                                )
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                if (!isSelectionModeActive) {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = { Text(title) }
                            )
                        }
                    }
                }
                HorizontalPager(state = pagerState) { page ->
                    val onMediaItemClick: (MediaItem) -> Unit = { mediaItem ->
                        if (isSelectionModeActive) {
                            viewModel.toggleSelection(mediaItem.id)
                        } else {
                            onMediaClick(mediaItem)
                        }
                    }
                    val onMediaItemLongClick: (MediaItem) -> Unit = { mediaItem ->
                        viewModel.toggleSelection(mediaItem.id)
                    }

                    when (page) {
                        0 -> ImageScreen(
                            mediaItems = images,
                            selectedIds = selectedIds,
                            viewMode = viewMode,
                            onMediaClick = onMediaItemClick,
                            onMediaLongClick = onMediaItemLongClick
                        )
                        1 -> VideoScreen(
                            mediaItems = videos,
                            selectedIds = selectedIds,
                            viewMode = viewMode,
                            onMediaClick = onMediaItemClick,
                            onMediaLongClick = onMediaItemLongClick
                        )
                        2 -> AudioScreen(
                            mediaItems = audios,
                            selectedIds = selectedIds,
                            viewMode = viewMode,
                            onMediaClick = onMediaItemClick,
                            onMediaLongClick = onMediaItemLongClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContextualTopAppBar(
    selectionCount: Int,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = { Text("$selectionCount selected") },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close selection mode")
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete selected items")
            }
        }
    )
}
