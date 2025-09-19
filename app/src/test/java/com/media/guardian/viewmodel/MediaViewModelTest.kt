package com.media.guardian.viewmodel

import android.app.Application
import android.net.Uri
import app.cash.turbine.test
import com.media.guardian.data.MediaItem
import com.media.guardian.data.SortColumn
import com.media.guardian.data.SortOption
import com.media.guardian.data.SortOrder
import com.media.guardian.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MediaViewModelTest {

    private lateinit var viewModel: MediaViewModel
    private lateinit var repository: MediaRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        // Mock Application context for the factory
        val application: Application = mockk(relaxed = true)
        // We don't use the factory directly, we pass the mocked repo
        viewModel = MediaViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val sampleMedia = listOf(
        MediaItem(1, Uri.parse("uri://c"), "Cat", 100, 3000, "image/jpeg"),
        MediaItem(2, Uri.parse("uri://a"), "Apple", 300, 1000, "image/jpeg"),
        MediaItem(3, Uri.parse("uri://b"), "Banana", 200, 2000, "image/jpeg")
    )

    @Test
    fun `loadMedia should populate media lists`() = runTest {
        coEvery { repository.getImages() } returns sampleMedia
        coEvery { repository.getVideos() } returns emptyList()
        coEvery { repository.getAudios() } returns emptyList()

        viewModel.loadMedia()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.images.test {
            assertEquals(sampleMedia, awaitItem())
        }
    }

    @Test
    fun `sorting by name ascending should order list correctly`() = runTest {
        coEvery { repository.getImages() } returns sampleMedia
        coEvery { repository.getVideos() } returns emptyList()
        coEvery { repository.getAudios() } returns emptyList()
        viewModel.loadMedia()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSortOptionChanged(SortOption(SortColumn.NAME, SortOrder.ASC))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.images.test {
            val sortedList = awaitItem()
            assertEquals("Apple", sortedList[0].displayName)
            assertEquals("Banana", sortedList[1].displayName)
            assertEquals("Cat", sortedList[2].displayName)
        }
    }

    @Test
    fun `searching should filter the list correctly`() = runTest {
        coEvery { repository.getImages() } returns sampleMedia
        coEvery { repository.getVideos() } returns emptyList()
        coEvery { repository.getAudios() } returns emptyList()
        viewModel.loadMedia()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Apple")
        testDispatcher.scheduler.advanceUntilIdle() // Debounce time

        viewModel.images.test {
            val filteredList = awaitItem()
            assertEquals(1, filteredList.size)
            assertEquals("Apple", filteredList[0].displayName)
        }
    }
}
