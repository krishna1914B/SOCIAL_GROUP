package com.socialapp.android.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.google.firebase.firestore.FirebaseFirestore
import com.socialapp.android.data.model.Media
import kotlinx.coroutines.tasks.await
import java.io.File

class MediaRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val mediaCollection = firestore.collection("albums")

    suspend fun uploadMedia(
        fileUri: Uri,
        uploaderName: String,
        uploaderId: String,
        isVideo: Boolean
    ): String? {
        return try {
            // Upload to Cloudinary
            val request = MediaManager.get().upload(fileUri)
            val cloudinaryResult = request.dispatch()
            
            // Get the URL from Cloudinary result
            val url = cloudinaryResult.get("secure_url")?.toString() ?: return null
            
            // Save metadata to Firestore
            val media = Media(
                id = mediaCollection.document().id,
                url = url,
                uploaderName = uploaderName,
                uploaderId = uploaderId,
                timestamp = System.currentTimeMillis(),
                isVideo = isVideo
            )
            
            mediaCollection.document(media.id).set(media).await()
            media.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllMedia(): List<Media> {
        return try {
            val snapshot = mediaCollection.orderBy("timestamp").get().await()
            snapshot.mapNotNull { it.toObject(Media::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}