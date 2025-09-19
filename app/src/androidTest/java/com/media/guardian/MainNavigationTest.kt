package com.media.guardian

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test

class MainNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // Pre-grant permissions for the test
    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.READ_MEDIA_VIDEO,
        android.Manifest.permission.READ_MEDIA_AUDIO
    )

    @Test
    fun appLaunches_andDisplaysMainScreen() {
        // After permissions are granted, the main screen should be visible.
        // We look for the title of the app in the top app bar.
        composeTestRule.onNodeWithText("Media Guardian").assertIsDisplayed()

        // We also check if the tabs are displayed.
        composeTestRule.onNodeWithText("Images").assertIsDisplayed()
        composeTestRule.onNodeWithText("Videos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Audio").assertIsDisplayed()
    }
}
