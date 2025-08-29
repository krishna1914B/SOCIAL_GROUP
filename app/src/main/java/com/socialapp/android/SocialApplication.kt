package com.socialapp.android

import android.app.Application
import com.cloudinary.android.MediaManager
import java.util.HashMap

class SocialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Cloudinary with actual credentials
        val config = HashMap<String, String>()
        config[MediaManager.CLOUDINARY_URL] = "cloudinary://${getString(R.string.cloudinary_api_key)}:${getString(R.string.cloudinary_api_secret)}@${getString(R.string.cloudinary_cloud_name)}"
        MediaManager.init(this, config)
    }
}