package com.socialapp.android.ui.album

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.socialapp.android.R
import com.socialapp.android.data.model.Media
import com.socialapp.android.ui.media.MediaViewModel
import com.socialapp.android.ui.user.UserViewModel

@Composable
fun AlbumScreen(
    mediaViewModel: MediaViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val mediaList by mediaViewModel.mediaList.collectAsState()
    val isLoading by mediaViewModel.isLoading.collectAsState()
    val uploadSuccess by mediaViewModel.uploadSuccess.collectAsState()
    val error by mediaViewModel.error.collectAsState()
    
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var isVideo by remember { mutableStateOf(false) }
    
    // Launcher for picking media
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                selectedMediaUri = it
                // Check if it's a video
                isVideo = isVideoFile(context, it)
                // Upload the media
                mediaViewModel.uploadMedia(it, isVideo)
            }
        }
    )
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                // Check permission before launching picker
                if (hasMediaPermission(context)) {
                    mediaPickerLauncher.launch("image/*,video/*")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.button_upload))
        }
        
        if (isLoading) {
            CircularProgressIndicator()
        }
        
        if (uploadSuccess) {
            Text(
                text = stringResource(R.string.upload_success),
                color = androidx.compose.ui.graphics.Color.Green,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        if (error != null) {
            Text(
                text = error ?: "",
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(mediaList) { media ->
                MediaItem(media = media)
            }
        }
    }
}

@Composable
fun MediaItem(media: Media) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Display media based on type
        if (media.isVideo) {
            // For videos, show a placeholder or thumbnail
            Text("Video: ${media.url}")
        } else {
            // For images, show the image
            Text("Image: ${media.url}")
        }
        
        Text(
            text = "Uploaded by: ${media.uploaderName}",
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Text(
            text = "Uploaded at: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(media.timestamp))}",
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

fun isVideoFile(context: Context, uri: Uri): Boolean {
    val mimeType = context.contentResolver.getType(uri)
    return mimeType?.startsWith("video/") == true
}

fun hasMediaPermission(context: Context): Boolean {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}