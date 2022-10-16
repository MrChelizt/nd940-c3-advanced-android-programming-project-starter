package com.udacity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CheckStatusReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val detailViewActivityIntent = Intent(context, DetailActivity::class.java)
        detailViewActivityIntent.putExtra("projectType", intent.getSerializableExtra("projectType") as ProjectEnum)
        detailViewActivityIntent.putExtra("status", intent.getSerializableExtra("status") as StatusEnum)
        detailViewActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(detailViewActivityIntent)
    }
}