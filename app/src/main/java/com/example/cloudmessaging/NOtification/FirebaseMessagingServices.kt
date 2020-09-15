package com.example.cloudmessaging.NOtification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.cloudmessaging.MainActivity
import com.example.cloudmessaging.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class FirebaseMessagingServices : FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        var notificationIntent : Intent? = null

        if (MainActivity.isAppRunning) {
            Log.d("Action", "onMessageReceived: "+"App Not Running")
        } else {
            notificationIntent = Intent(this, MainActivity::class.java)
        }

        notificationIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,
            0 /* Request code */, notificationIntent,
            PendingIntent.FLAG_ONE_SHOT)

        //You should use an actual ID instead
        val notificationId = Random.nextInt(60000)
        remoteMessage?.let {
            val bitmap = getBitmapFromUrl(remoteMessage.notification!!.imageUrl.toString())

            val likeIntent = Intent(this, LinkService::class.java)
            likeIntent.putExtra(NOTIFICATION_ID_EXTRA, notificationId)
            likeIntent.putExtra(IMAGE_URL_EXTRA, remoteMessage.notification!!.imageUrl)
            val likePendingIntent = PendingIntent.getService(this,
                notificationId + 1, likeIntent, PendingIntent.FLAG_ONE_SHOT)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels()
            }

            val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setLargeIcon(bitmap)
                .setContentTitle(remoteMessage.getNotification()!!.getTitle())
                .setContentText(remoteMessage.getNotification()!!.getBody())
                .setStyle(NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.notification)
                .addAction(R.drawable.notification,
                    getString(R.string.notification_add_to_cart_button), likePendingIntent)
                .setContentIntent(pendingIntent)

            notificationManager?.notify(notificationId, notificationBuilder.build())
        }
    }

    override fun onNewToken(token: String) {
        Log.d("TAG", "Refreshed token: $token")
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.edit().putString(Constants.FIREBASE_TOKEN, token).apply()
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)

    }

    companion object {

        private const val NOTIFICATION_ID_EXTRA = "notificationId"
        private const val IMAGE_URL_EXTRA = "imageUrl"
        private const val ADMIN_CHANNEL_ID = "admin_channel"
    }
}