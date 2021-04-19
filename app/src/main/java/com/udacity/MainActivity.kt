package com.udacity

import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.webkit.URLUtil
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton = findViewById(R.id.custom_button)

        custom_button.setOnClickListener {
            filterOption()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            loadingButton.buttonState = ButtonState.Completed
        }
    }

    private fun download(url: String) {
        loadingButton.buttonState = ButtonState.Clicked
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        loadingButton.buttonState = ButtonState.Loading
    }

    private fun filterOption(){
        when {
            glide_button.isChecked -> {
                download(URL_GLIDE)
            }
            retrofit_button.isChecked -> {
                download(URL_RETROFIT)
            }
            loadapp_button.isChecked -> {
                download(URL_LOADAPP)
            }
            custom_url_button.isChecked->{
                val customURL = edit_custom_url.text.toString()
                if(URLUtil.isValidUrl(customURL)){
                    download(customURL)
                }else{
                    edit_custom_url.error = getString(R.string.error_message)
                }
            }
            else -> {
                Toast.makeText(applicationContext, getString(R.string.toast_message), Toast.LENGTH_LONG).show()
                loadingButton.buttonState = ButtonState.Clicked
                loadingButton.buttonState = ButtonState.Completed
            }
        }
    }

    companion object {
        private const val URL_LOADAPP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
