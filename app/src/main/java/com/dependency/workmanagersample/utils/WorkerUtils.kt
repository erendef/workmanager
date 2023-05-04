@file:JvmName("WorkerUtils")

package com.dependency.workmanagersample.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dependency.workmanagersample.R


private const val TAG = "WorkerUtils"
const val DELAY_TIME: Long = 3000
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
@JvmField val NOTIFICATION_TITLE: CharSequence = "WorkManager"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1
const val KEY_ICON_TEXT = "KEY_ICON_TEXT"
const val KEY_SDK_VERSION_TEXT = "KEY_SDK_VERSION_TEXT"
const val DEFAULT_NOTIFICATION_TEXT = "Setting DefineX default icon."
const val ALTERNATE_NOTIFICATION_TEXT = "Setting alternate icon."
const val SDK_VERSION_Q_TEXT = "Application is closing…"
const val SDK_VERSION_UNDER_Q_TEXT = "Changing icon…"
const val SUCCESS_TEXT = "Work is successfully finished."
const val ICON_CHANGE_WORK_NAME = "icon_change_work_name"
const val TAG_OUTPUT = "OUTPUT"

fun makeStatusNotification(message: String, context: Context) {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Show the notification
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

fun isAliasEnabled(applicationContext:Context, aliasName: String): Boolean {
    return applicationContext.packageManager.getComponentEnabledSetting(
        ComponentName(
            "com.dependency.workmanagersample",
            aliasName
        )
    ) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
}

fun sleep() {
    try {
        Thread.sleep(DELAY_TIME, 0)
    } catch (e: InterruptedException) {
        Log.e(TAG, e.message.toString())
    }

}

@WorkerThread
fun changeAppIcon(type : IconType, applicationContext : Context) {

    Log.i("Icon Changing...","Process starting")

    applicationContext.packageManager.setComponentEnabledSetting(
        ComponentName("com.dependency.workmanagersample",type.cls),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
    )
    IconType.values().forEach {
        if (it == type) return@forEach
        applicationContext.packageManager.setComponentEnabledSetting(
            ComponentName("com.dependency.workmanagersample", type.cls),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

}


enum class IconType(val cls: String) {
    DEFAULT("com.dependency.workmanagersample.MainActivityDEFAULT"),
    ALTERNATE("com.dependency.workmanagersample.MainActivityALTERNATE")
}