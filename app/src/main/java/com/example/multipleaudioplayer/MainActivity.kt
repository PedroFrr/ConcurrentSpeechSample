package com.example.multipleaudioplayer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.multipleaudioplayer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var bluetoothHeadset: BluetoothHeadset? = null

    // Get the default adapter
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Create channel notification for app
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_HIGH, false,
            getString(R.string.app_name), "App notification channel."
        )

        // Establish connection to the proxy.
        bluetoothAdapter.getProfileProxy(this, profileListener, BluetoothProfile.HEADSET)

        val receiver = HeadsetStateBroadcastReceiver()

        // Intent Filter is useful to determine which apps wants to receive
        // which intents,since here we want to respond to change of
        // airplane mode
        val intentFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        intentFilter.addAction("android.bluetooth.headset.action.STATE_CHANGED")
        intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")
        registerReceiver(receiver, intentFilter)

    }

    /*
     * If headphone is available we enable the spatial checkbox, disable it otherwise
     * This is only useful at the beginning of the app,
     * afterwards its the broadcastReceiver responsibility to enable/disable the checkbox
     */
    private fun isWiredHeadphoneAvailable(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (deviceInfo in audioDevices) {
            if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                || deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
            ) {
                return true
            }
        }
        return false
    }

    // Define Service Listener of BluetoothProfile
    private val profileListener: ServiceListener = object : ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = proxy as BluetoothHeadset
                Log.i("", isBluetoothHeadsetConnected().toString())
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = null
                Log.i("TAG", isBluetoothHeadsetConnected().toString())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun isBluetoothHeadsetConnected(): Boolean {
        val devices: List<BluetoothDevice> = bluetoothHeadset?.connectedDevices ?: return false

        return devices.isNotEmpty()

    }

    override fun onStop() {
        bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
        super.onStop()
    }
}