package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val notificationManager =
            ContextCompat.getSystemService(
                application,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        val projectType = intent.getSerializableExtra("projectType") as ProjectEnum
        val status = intent.getSerializableExtra("status") as StatusEnum

        fileNameTextView.text = projectType.fileName
        statusTextView.text = status.desc

        okButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}
