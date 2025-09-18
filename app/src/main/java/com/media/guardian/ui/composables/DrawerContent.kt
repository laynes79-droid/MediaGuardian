package com.media.guardian.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.media.guardian.data.SortColumn
import com.media.guardian.data.SortOption
import com.media.guardian.data.SortOrder
import com.media.guardian.viewmodel.MediaViewModel

@Composable
fun DrawerContent(
    viewModel: MediaViewModel,
    onSortChanged: () -> Unit,
    onTagSelected: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val allTags by viewModel.allTags.collectAsState(initial = emptyList())
    val selectedTag by viewModel.selectedTag.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Search Section
        Text("Search", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            label = { Text("Search by name...") },
            modifier = Modifier.fillMaxWidth()
        )

        // Tags Section
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Filter by Tag", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        if (selectedTag != null) {
            Button(onClick = {
                viewModel.onTagSelected(null)
                onTagSelected()
            }) {
                Text("Clear Tag Filter")
            }
        }
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(allTags) { tag ->
                SuggestionChip(
                    onClick = {
                        viewModel.onTagSelected(tag.name)
                        onTagSelected()
                    },
                    label = { Text(tag.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }


        // Sort Section
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sort by", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val onSortClick = { option: SortOption ->
            viewModel.onSortOptionChanged(option)
            onSortChanged()
        }

        SortGroup(
            title = "Date",
            currentSort = sortOption,
            sortColumn = SortColumn.DATE,
            onSelect = onSortClick
        )
        SortGroup(
            title = "Name",
            currentSort = sortOption,
            sortColumn = SortColumn.NAME,
            onSelect = onSortClick
        )
        SortGroup(
            title = "Size",
            currentSort = sortOption,
            sortColumn = SortColumn.SIZE,
            onSelect = onSortClick
        )
    }
}

@Composable
private fun SortGroup(
    title: String,
    currentSort: SortOption,
    sortColumn: SortColumn,
    onSelect: (SortOption) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title)
        Row {
            Button(
                onClick = { onSelect(SortOption(sortColumn, SortOrder.ASC)) },
                enabled = !(currentSort.column == sortColumn && currentSort.order == SortOrder.ASC)
            ) { Text("Ascending") }
            Button(
                onClick = { onSelect(SortOption(sortColumn, SortOrder.DESC)) },
                enabled = !(currentSort.column == sortColumn && currentSort.order == SortOrder.DESC),
                modifier = Modifier.padding(start = 8.dp)
            ) { Text("Descending") }
        }
    }
}
