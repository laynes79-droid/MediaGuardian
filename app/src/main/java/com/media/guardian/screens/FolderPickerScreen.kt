package com.media.guardian.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.media.guardian.navigation.Screen
import com.media.guardian.viewmodel.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderPickerScreen(
    navController: NavController,
    viewModel: MediaViewModel,
    mediaItemId: Long,
    operationType: String
) {
    val context = LocalContext.current
    val folders by viewModel.folders.collectAsState()
    val mediaItem = viewModel.getMediaItemById(mediaItemId)

    LaunchedEffect(Unit) {
        viewModel.loadFolders()
    }

    if (mediaItem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Media item not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Destination Folder") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(folders) { folderName ->
                Text(
                    text = folderName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val onResult = { success: Boolean ->
                                val message = if (success) {
                                    "$operationType successful"
                                } else {
                                    "$operationType failed"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }

                            when (operationType) {
                                "copy" -> viewModel.copyMedia(mediaItem, folderName, onResult)
                                "move" -> viewModel.moveMedia(mediaItem, folderName, onResult)
                            }
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}
