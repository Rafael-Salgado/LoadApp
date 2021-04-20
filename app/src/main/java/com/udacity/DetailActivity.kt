package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

private const val FILE_NAME = "FILE NAME"
private const val STATUS_STATE = "STATUS"

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileText = intent.getStringExtra(FILE_NAME)
        val status = intent.getBooleanExtra(STATUS_STATE, false)
        setStatus(status)
        file_name_content.text = fileText
        ok_button.setOnClickListener {
            finish()
        }
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()
    }

    private fun setStatus(statusState: Boolean) {
        if (statusState) {
            status_text.text = getString(R.string.status_success)
            val color = getColor(R.color.success_color)
            status_text.setTextColor(color)
        } else {
            status_text.text = getString(R.string.status_fail)
            val color = getColor(R.color.fail_color)
            status_text.setTextColor(color)
        }
    }

}
