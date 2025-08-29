package com.socialapp.android.ui.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.socialapp.android.data.model.User
import com.socialapp.android.ui.user.UserViewModel

@Composable
fun FriendsScreen(
    userViewModel: UserViewModel,
    onSignOut: () -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val allUsers by userViewModel.allUsers.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FRIENDS",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (isLoading) {
            Text("Loading friends...")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(allUsers) { user ->
                    UserItem(
                        user = user,
                        isCurrentUser = user.id == currentUser?.id
                    )
                }
            }
        }

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Sign Out")
        }
    }
}

@Composable
fun UserItem(user: User, isCurrentUser: Boolean) {
    val textStyle = if (isCurrentUser) {
        MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    } else {
        MaterialTheme.typography.bodyLarge
    }

    Text(
        text = user.name,
        style = textStyle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}