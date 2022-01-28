package com.example.multipleaudioplayer

import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class HeadsetStateBroadcastReceiver : BroadcastReceiver() {

    // this function will be executed when the user changes his
    // airplane mode
    override fun onReceive(context: Context, intent: Intent) {

        var isConnected = false

        // Wired headset monitoring
        if (intent.action.equals(HEADPHONE_ACTIONS[0])) {
            val state = intent.getIntExtra("state", 0)
            isConnected = state == BluetoothProfile.STATE_CONNECTED
        }

        // Bluetooth monitoring
        // Works up to and including Honeycomb
        if (intent.action.equals(HEADPHONE_ACTIONS[1])) {
            val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0)
            isConnected = state == BluetoothProfile.STATE_CONNECTED
        }

        // Works for Ice Cream Sandwich
        if (intent.action.equals(HEADPHONE_ACTIONS[2])) {
            val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0)
            isConnected = state == BluetoothProfile.STATE_CONNECTED
        }

        // Used to inform interested activities that the headset state has changed
        if (isConnected) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("headsetStateChange"))
        }
    }

    companion object {
        val HEADPHONE_ACTIONS = arrayOf(
            Intent.ACTION_HEADSET_PLUG,
            "android.bluetooth.headset.action.STATE_CHANGED",
            BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED
        )
    }

}