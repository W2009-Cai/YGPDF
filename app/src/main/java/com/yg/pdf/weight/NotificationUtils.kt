package com.yg.pdf.weight

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.yg.pdf.MainActivity
import com.yg.pdf.R

object NotificationUtils {
    private lateinit var mManager: NotificationManager
    private const val mCustomNotificationId = 9009 // 通知id

      fun initNotificationCompat(application: Application) {
        mManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 添加自定义通知view
        val views = RemoteViews(application.packageName, R.layout.notification_layout)
        val pendingIntent = PendingIntent.getActivity(
            application,
            222,
            Intent(application, MainActivity::class.java),
            flag
        )
        views.setOnClickPendingIntent(R.id.picture_tv, pendingIntent)
        views.setOnClickPendingIntent(R.id.pdf_tv, pendingIntent)
        // 创建Builder
        // 8.0 以上需要特殊处理
        val channelId: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("com.yg.pdf", "Notification", application)
        } else {
            ""
        }
        val notification = NotificationCompat.Builder(application, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setVibrate(LongArray(0))
            .setSound(null)
            .setCustomContentView(views)
            .setCustomBigContentView(views)
            .build()// 设置自定义通知view
        mManager.notify(mCustomNotificationId, notification)
    }

    /**
     * 创建通知通道
     * @param channelId
     * @param channelName
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        application: Application
    ): String {
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.setSound(null, null)
        chan.vibrationPattern = LongArray(0)
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        mManager.createNotificationChannel(chan)
        return channelId
    }

    val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
}