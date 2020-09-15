package com.example.cloudmessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.example.cloudmessaging.NOtification.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "1"
        val channel2 = "2"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,
                    "Channel 1", NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.description = "This is BNT"
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(notificationChannel)

            val notificationChannel2 = NotificationChannel(channel2,
                    "Channel 2", NotificationManager.IMPORTANCE_MIN)

            notificationChannel.description = "This is bTV"
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(notificationChannel2)

            val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            Log.d("TAGKEY", "onCreate: "+preferences.getString(Constants.FIREBASE_TOKEN,"token"))

            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            //To do//
                            return@OnCompleteListener
                        }

                        // Get the Instance ID token//
                        val token = task.result!!.token
                        val msg = getString(R.string.fcm_token, token)
                        Log.d("TOKEN", msg)
                    })

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        isAppRunning = false
    }
    companion object {

        var isAppRunning: Boolean = false
    }
}