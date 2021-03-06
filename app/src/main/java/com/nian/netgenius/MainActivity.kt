package com.nian.netgenius

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.form.*
import java.io.File
import java.io.IOException

fun vpnStatusToToggleLevel(status: Int): Int = when (status) {
    VPN_STATUS_STOPPED -> 0
    VPN_STATUS_RUNNING -> 2
    else -> 1
}

fun vpnStatusShouldStop(status: Int): Boolean = when (status) {
    VPN_STATUS_STOPPED -> false
    else -> true
}

class MainActivity : Activity(), View.OnClickListener {
    companion object {
        private val TAG = "MainActivity"
    }

    var vpnServiceBroadcastReceiver = broadcastReceiver() { context, intent ->
        val str_id = intent.getIntExtra(VPN_UPDATE_STATUS_EXTRA, R.string.notification_stopped)
        updateStatus(str_id)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form)
        findViewById(R.id.settings).setOnClickListener(this)

        val path = filesDir.parent
        val file = File(path + "/files/user_custom.txt")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        // Should we make sure the vpn service is started already based o the preferences?

        vpn_toggle.setOnClickListener {
            if (vpnStatusShouldStop(AdVpnService.vpnStatus)) {
                Log.i(TAG, "Attempting to disconnect")

                val intent = Intent(this, AdVpnService::class.java)
                intent.putExtra("COMMAND", Command.STOP.ordinal)
                startService(intent)
            } else {
                Log.i(TAG, "Attempting to connect")

                val intent = VpnService.prepare(this)
                if (intent != null) {
                    startActivityForResult(intent, 0)
                } else {
                    onActivityResult(0, RESULT_OK, null)
                }

            }
        }
    }

    private fun updateStatus(status: Int) {
        text_status.text = getString(vpnStatusToTextId(status))
        val level = vpnStatusToToggleLevel(status)
        vpn_toggle.setImageLevel(level)
    }

    override fun onActivityResult(request: Int, result: Int, data: Intent?) {
        if (result == RESULT_OK) {
            val intent = Intent(this, AdVpnService::class.java)
            intent.putExtra("COMMAND", Command.START.ordinal)
            intent.putExtra("NOTIFICATION_INTENT",
                    PendingIntent.getActivity(this, 0,
                            Intent(this, MainActivity::class.java), 0))
            startService(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(vpnServiceBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()

        updateStatus(AdVpnService.vpnStatus)
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(vpnServiceBroadcastReceiver, IntentFilter(VPN_UPDATE_STATUS_INTENT))
    }

    override fun onClick(v: View) {
        if (v === findViewById(R.id.settings)){
            intent = Intent(this, Settings::class.java)
            startActivity(intent);
        }
    }
}
