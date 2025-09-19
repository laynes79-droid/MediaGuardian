package com.media.guardian.viewmodel

import android.app.Application
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.media.guardian.data.MediaItem
import com.media.guardian.data.SortColumn
import com.media.guardian.data.SortOption
import com.media.guardian.data.SortOrder
import com.media.guardian.database.AppDatabase
import com.media.guardian.database.Tag
import com.media.guardian.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.media.guardian.data.ViewMode
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MediaViewModel(private val repository: MediaRepository) : ViewModel() {

    // --- Internal Data Sources ---
    private val _allImages = MutableStateFlow<List<MediaItem>>(emptyList())
    private val _allVideos = MutableStateFlow<List<MediaItem>>(emptyList())
    private val _allAudios = MutableStateFlow<List<MediaItem>>(emptyList())

    // --- UI State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption(SortColumn.DATE, SortOrder.DESC))
    val sortOption = _sortOption.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag = _selectedTag.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.GRID_NORMAL)
    val viewMode = _viewMode.asStateFlow()

    val allTags: Flow<List<Tag>> = repository.getAllTags()

    // --- Filtered & Sorted Data for UI ---
    val images = combine(_allImages, _searchQuery.debounce(300), _sortOption) { list, query, sort ->
        applyFilterAndSort(list, query, sort)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videos = combine(_allVideos, _searchQuery.debounce(300), _sortOption) { list, query, sort ->
        applyFilterAndSort(list, query, sort)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audios = combine(_allAudios, _searchQuery.debounce(300), _sortOption) { list, query, sort ->
        applyFilterAndSort(list, query, sort)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    val isSelectionModeActive = _selectedIds.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _deleteRequest = MutableSharedFlow<IntentSenderRequest>()
    val deleteRequest = _deleteRequest.asSharedFlow()

    init {
        // Reload media whenever the selected tag changes
        _selectedTag.onEach { tag ->
            loadMedia(tag)
        }.launchIn(viewModelScope)
    }

    private fun loadMedia(tag: String? = null) {
        viewModelScope.launch {
            _allImages.value = repository.getImages(tag)
            _allVideos.value = repository.getVideos(tag)
            _allAudios.value = repository.getAudios(tag)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortOptionChanged(option: SortOption) {
        _sortOption.value = option
    }

    fun onTagSelected(tagName: String?) {
        _selectedTag.value = tagName
    }

    fun onViewModeChanged(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun getTagsForMediaItem(mediaItemId: Long) = repository.getTagsForMediaItem(mediaItemId)

    fun addTagToMediaItem(mediaItem: MediaItem, tagName: String) {
        viewModelScope.launch {
            repository.addTagToMediaItem(mediaItem, tagName)
        }
    }

    fun removeTagFromMediaItem(mediaItemId: Long, tagName: String) {
        viewModelScope.launch {
            repository.removeTagFromMediaItem(mediaItemId, tagName)
        }
    }

    private fun applyFilterAndSort(list: List<MediaItem>, query: String, sort: SortOption): List<MediaItem> {
        val filteredList = if (query.isBlank()) {
            list
        } else {
            list.filter { it.displayName.contains(query, ignoreCase = true) }
        }

        return when (sort.column) {
            SortColumn.NAME -> if (sort.order == SortOrder.ASC) filteredList.sortedBy { it.displayName } else filteredList.sortedByDescending { it.displayName }
            SortColumn.DATE -> if (sort.order == SortOrder.ASC) filteredList.sortedBy { it.dateAdded } else filteredList.sortedByDescending { it.dateAdded }
            SortColumn.SIZE -> if (sort.order == SortOrder.ASC) filteredList.sortedBy { it.size } else filteredList.sortedByDescending { it.size }
        }
    }

    fun toggleSelection(id: Long) {
        val currentSelection = _selectedIds.value.toMutableSet()
        if (id in currentSelection) {
            currentSelection.remove(id)
        } else {
            currentSelection.add(id)
        }
        _selectedIds.value = currentSelection
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelectedFiles() {
        viewModelScope.launch {
            val allMedia = _allImages.value + _allVideos.value + _allAudios.value
            val urisToDelete = _selectedIds.value.mapNotNull { id ->
                allMedia.find { it.id == id }?.uri
            }

            if (urisToDelete.isNotEmpty()) {
                val pendingIntent = repository.deleteFiles(urisToDelete)
                if (pendingIntent != null) {
                    _deleteRequest.emit(IntentSenderRequest.Builder(pendingIntent).build())
                } else {
                    // Deletion was handled directly on older API, just refresh
                    loadMedia(selectedTag.value)
                    clearSelection()
                }
            }
        }
    }

    fun duplicateMedia(uri: Uri) {
        viewModelScope.launch {
            repository.duplicateMedia(uri)
            loadMedia(_selectedTag.value)
        }
    }

    fun getMediaItemById(id: Long): MediaItem? {
        val allMedia = _allImages.value + _allVideos.value + _allAudios.value
        return allMedia.find { it.id == id }
    }
}

class MediaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { MediaRepository(application.applicationContext, database.tagDao()) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
