package com.example.cloudmessaging.NOtification

import android.app.Service
import android.content.Intent
import android.os.IBinder


class LinkService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {

        private const val NOTIFICATION_ID_EXTRA = "notificationId"
        private const val IMAGE_URL_EXTRA = "imageUrl"
    }
}