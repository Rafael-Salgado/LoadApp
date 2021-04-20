package com.udacity


import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val FILE_NAME = "FILE NAME"
private const val STATUS_STATE = "STATUS"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var loadingButton: LoadingButton

    private var fileName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton = findViewById(R.id.custom_button)

        custom_button.setOnClickListener {
            filterOption()
        }
        notificationManager =  this.getSystemService(NotificationManager::class.java)
        notificationManager.createChannel(this)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val status = getStatus(id)
            loadingButton.buttonState = ButtonState.Completed
            val contentIntent = Intent(applicationContext, DetailActivity::class.java)
            contentIntent.putExtra(FILE_NAME,fileName)
            contentIntent.putExtra(STATUS_STATE,status)
            notificationManager.sendNotification(applicationContext,contentIntent)
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

    private fun filterOption() {
        when {
            glide_button.isChecked -> {
                download(URL_GLIDE)
                fileName = getString(R.string.glide_text)
            }
            retrofit_button.isChecked -> {
                download(URL_RETROFIT)
                fileName = getString(R.string.retrofit_text)
            }
            loadapp_button.isChecked -> {
                download(URL_LOADAPP)
                fileName = getString(R.string.loadapp__text)
            }
            custom_url_button.isChecked -> {
                val customURL = edit_custom_url.text.toString()
                if (URLUtil.isValidUrl(customURL)) {
                    download(customURL)
                    fileName = getString(R.string.customurl_text)
                } else {
                    edit_custom_url.error = getString(R.string.error_message)
                }
            }
            else -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_message),
                    Toast.LENGTH_LONG
                ).show()
                loadingButton.buttonState = ButtonState.Clicked
                loadingButton.buttonState = ButtonState.Completed
            }
        }
    }

    private fun getStatus (id: Long?): Boolean{
        var statusState = false
        val query = DownloadManager.Query().setFilterById(id!!)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()){
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) statusState = true
        }
        return statusState
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
