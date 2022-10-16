package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var projectType: ProjectEnum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(CHANNEL_ID, CHANNEL_NAME)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            when (radioGroup.checkedRadioButtonId) {
                R.id.glideButton -> {
                    projectType = ProjectEnum.GLIDE
                    download(GLIDE_URL)
                }
                R.id.loadAppButton -> {
                    projectType = ProjectEnum.LOAD_APP
                    download(LOAD_APP_URL)
                }
                R.id.retrofitButton -> {
                    projectType = ProjectEnum.RETROFIT
                    download(RETROFIT_URL)
                }
                else -> {
                    projectType = null
                    Toast.makeText(
                        baseContext,
                        getString(R.string.select_project_warning),
                        Toast.LENGTH_SHORT
                    ).show()
                    custom_button.buttonState = ButtonState.Completed
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            query.setFilterById(id ?: downloadID)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                if (cursor.count > 0) {
                    val statusI = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    when (cursor.getInt(statusI)) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.e("Download status", "success")
                            custom_button.buttonState =
                                ButtonState.Completed
                            sendNotification(StatusEnum.SUCCESS)
                        }
                        DownloadManager.STATUS_FAILED -> {
                            Log.e("Download status", "fail")
                            custom_button.buttonState =
                                ButtonState.Completed
                            sendNotification(StatusEnum.FAIL)
                        }
                    }
                }
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private fun checkPermissions(): Boolean {
        if (isNetworkAvailable(this)) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PermissionInfo.PROTECTION_DANGEROUS
                )
            }
        }
        custom_button.buttonState = ButtonState.Completed
        return false
    }

    private fun download(url: String) {
        if (!checkPermissions()) return
        custom_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/project.zip")
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Load app download information"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(status: StatusEnum) {
        val notificationManager =
            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.sendNotification(CHANNEL_ID, projectType!!, status, applicationContext)
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "loadAppChannelId"
        private const val CHANNEL_NAME = "loadAppChannel"
    }

}
