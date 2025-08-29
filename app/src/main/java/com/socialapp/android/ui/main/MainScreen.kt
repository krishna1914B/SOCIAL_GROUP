package com.socialapp.android.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.socialapp.android.R
import com.socialapp.android.ui.album.AlbumScreen
import com.socialapp.android.ui.friends.FriendsScreen
import com.socialapp.android.ui.media.MediaViewModel
import com.socialapp.android.ui.user.UserViewModel

@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    mediaViewModel: MediaViewModel,
    onSignOut: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.title_friends),
        stringResource(R.string.title_album)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> FriendsScreen(
                userViewModel = userViewModel,
                onSignOut = onSignOut
            )
            1 -> AlbumScreen(
                mediaViewModel = mediaViewModel,
                userViewModel = userViewModel
            )
        }
    }
}