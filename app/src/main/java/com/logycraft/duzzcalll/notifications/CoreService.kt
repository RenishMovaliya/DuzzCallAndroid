/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linhome-android
 * (see https://www.linhome.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.logycraft.duzzcalll.notifications

//import org.linphone.core.tools.service.CoreManager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.logycraft.duzzcalll.Activity.IncomingActivity
import com.logycraft.duzzcalll.R
import org.linphone.core.Address
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.Factory
import org.linphone.core.tools.compatibility.DeviceUtils
import org.linphone.core.tools.service.CoreManager
import org.linphone.core.tools.service.CoreService
import org.linphone.mediastream.Version


var NOTIF_ID = 1
var NOTIFICATION_CHANNEL_ID = "org_linphone_core_service_notification_channel"
var NOTIFICATION_CHANNEL_NAME = "Linphone Core Service"
var NOTIFICATION_CHANNEL_DESC = "Used to keep the call(s) alive"
var NOTIFICATION_TITLE = "Duzzcall"
var NOTIFICATION_CONTENT = "Duzzcall user calling you"
var ActonButton = "Accept"
var ActonButton2 = "Decline"
var mIsInForegroundMode = false
var mServiceNotification: Notification? = null

private var mListener: CoreListenerStub? = null
private var mVibrator: Vibrator? = null
private var mIsVibrating = false
private var mAudioManager: AudioManager? = null

private var mIsListenerAdded = false

class CoreService : CoreService() {
    override fun onCreate() {
        super.onCreate()


        // No-op, just to ensure libraries have been loaded and thus prevent crash in log below
        // if service has been started directly by Android (that can happen...)
        Factory.instance()

        if (Version.sdkAboveOrEqual(Version.API26_O_80)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createServiceNotificationChannel()
            } else {
                org.linphone.core.tools.Log.d("SiPTEST", "One ")
            }
        }

        mVibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        mListener = object : CoreListenerStub() {
            override fun onFirstCallStarted(core: Core) {
                org.linphone.core.tools.Log.i("[Core Service] First call started")
                // There is only one call, service shouldn't be in foreground mode yet
                if (!mIsInForegroundMode) {
                    startForeground()
                }

                // Starting Android 10 foreground service is a requirement to be able to vibrate if app is in background
                val call = core.currentCall
                if (call != null) {
                    if (call.dir == Call.Dir.Incoming && core.isVibrationOnIncomingCallEnabled) {
                        vibrate(call.remoteAddress)
                    }
                } else {
                    org.linphone.core.tools.Log.w("[Core Service] Couldn't find current call...")
                }
            }

            override fun onCallStateChanged(
                core: Core,
                call: Call,
                state: Call.State,
                message: String
            ) {
                if (state == Call.State.End || state == Call.State.Error || state == Call.State.Connected) {
                    if (mIsVibrating) {
                        org.linphone.core.tools.Log.i("[Core Service] Stopping vibrator")
                        mVibrator!!.cancel()
                        mIsVibrating = false
                    }
                }
            }

            override fun onLastCallEnded(core: Core) {
                org.linphone.core.tools.Log.i("[Core Service] Last call ended")
                if (mIsInForegroundMode) {
                    stopForeground()
                }
            }
        }
        addCoreListener()

        org.linphone.core.tools.Log.i("[Core Service] Created")
    }

    //    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        if (intent?.extras?.get("StartForeground") == true) {
//            Log.i("[Service] Starting as foreground")
//            coreContext.notificationsManager.startForeground(this, true)
//        } else if (corePreferences.keepServiceAlive) {
//            coreContext.notificationsManager.startForeground(this, false)
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    override fun createServiceNotificationChannel() {
//        // Done elsewhere
//    }
//
//    override fun showForegroundServiceNotification() {
//        coreContext.notificationsManager.startCallForeground(this)
//    }
//
//    override fun hideForegroundServiceNotification() {
//        coreContext.notificationsManager.stopCallForeground()
//    }
//
//    override fun onTaskRemoved(rootIntent: Intent?) {
//        if (!corePreferences.keepServiceAlive) {
//            Log.i("[Service] Task removed, stopping Core")
//            coreContext.stop()
//        }
//
//        super.onTaskRemoved(rootIntent)
//    }
//
//    override fun onDestroy() {
//        Log.i("[Service] Stopping")
//        coreContext.notificationsManager.service = null
//
//        super.onDestroy()
//    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        org.linphone.core.tools.Log.i("[Core Service] Started")
        if (!mIsListenerAdded) {
            addCoreListener()
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        org.linphone.core.tools.Log.i("[Core Service] Task removed")
        super.onTaskRemoved(rootIntent)
    }

    @Synchronized
    override fun onDestroy() {
        org.linphone.core.tools.Log.i("[Core Service] Stopping")
        if (CoreManager.isReady()) {
            val core = CoreManager.instance().core
            if (core != null) {
                org.linphone.core.tools.Log.i("[Core Service] Core Manager found, removing our listener")
                core.removeListener(mListener)
            }
//            CoreManager.instance().isServiceRunningAsForeground = false
//                        notificationsManager.startForeground(this, false)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun addCoreListener() {
        org.linphone.core.tools.Log.i("[Core Service] Trying to add the Service's CoreListener to the Core...")
        if (CoreManager.isReady()) {
            val core = CoreManager.instance().core
            if (core != null) {
                org.linphone.core.tools.Log.i("[Core Service] Core Manager found, adding our listener")
                core.addListener(mListener)
                mIsListenerAdded = true
                org.linphone.core.tools.Log.i("[Core Service] CoreListener succesfully added to the Core")
                if (core.callsNb > 0) {
                    org.linphone.core.tools.Log.w("[Core Service] Core listener added while at least one call active !")
                    startForeground()
                    val call = core.currentCall
                    if (call != null) {
                        // Starting Android 10 foreground service is a requirement to be able to vibrate if app is in background
                        if (call.dir == Call.Dir.Incoming && call.state == Call.State.IncomingReceived && core.isVibrationOnIncomingCallEnabled) {
                            vibrate(call.remoteAddress)
                        }
                    } else {
                        org.linphone.core.tools.Log.w("[Core Service] Couldn't find current call...")
                    }
                }
            } else {
                org.linphone.core.tools.Log.e("[Core Service] CoreManager instance found but Core is null!")
            }
        } else {
            org.linphone.core.tools.Log.w("[Core Service] CoreManager isn't ready yet...")
        }
    }

    /* Foreground notification related */

    /* Foreground notification related */
    @RequiresApi(api = Build.VERSION_CODES.O)
    /**
     * This method should create a notification channel for the foreground service notification.
     * On Android < 8 it is not called.
     */
    override fun createServiceNotificationChannel() {
        org.linphone.core.tools.Log.i("[Core Service] Android >= 8.0 detected, creating notification channel")
        val notificationManager = NotificationManagerCompat.from(this)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description =
            NOTIFICATION_CHANNEL_DESC
        channel.enableVibration(false)
        channel.enableLights(false)
        channel.setShowBadge(false)

        notificationManager.createNotificationChannel(channel)
    }

    /**
     * This method should create a notification for the foreground service notification.
     * Remember on Android > 8 to use the notification channel created in createServiceNotificationChannel().
     */
    override fun createServiceNotification() {
        var intentAction = Intent(this, IncomingActivity::class.java)

        var Acceptclick = Intent(this, IncomingActivity::class.java)
        var Declineclick = Intent(this, IncomingActivity::class.java)
        Acceptclick.putExtra("action","Acceptclick");
        Declineclick.putExtra("action","Declineclick");
        notificationChannel()
        var ints: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ints = PendingIntent.FLAG_MUTABLE
        } else {
            ints = PendingIntent.FLAG_ONE_SHOT

        }

        Acceptclick.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this,0,Acceptclick,0)

        if (CoreManager.isReady()) {
            val core = CoreManager.instance().core
            val call = core.currentCall
            if (call != null) {
                org.linphone.core.tools.Log.w("[call] Call Nulled !")

                // Starting Android 10 foreground service is a requirement to be able to vibrate if app is in background
                if (call.dir == Call.Dir.Incoming && call.state == Call.State.IncomingReceived && core.isVibrationOnIncomingCallEnabled) {
//                            vibrate(call.remoteAddress)
                    NOTIFICATION_TITLE = "Duzzcall"
                    NOTIFICATION_CONTENT = "Duzzcall user calling you"
                     ActonButton = "Accept"
                     ActonButton2 = "Decline"
                    intentAction = Intent(this, IncomingActivity::class.java)

                } else  {
                    NOTIFICATION_TITLE = "Duzzcall"
                    NOTIFICATION_CONTENT = "Outgoing Call Running"
                    ActonButton = ""
                    ActonButton2 = ""
//                    intentAction = Intent(this, null)
                }
            }
        }
        val pendingIntent1 = PendingIntent.getActivity(this, 0, intentAction, ints)
        mServiceNotification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            // adding notification Title
            .setContentTitle(NOTIFICATION_TITLE)
            // adding notification Text
            .setContentText(NOTIFICATION_CONTENT)
            // adding notification SmallIcon
            .setSmallIcon(applicationInfo.icon)
            // adding notification Priority
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // making the notification clickable
            .setContentIntent(pendingIntent1)
            .setAutoCancel(true)
            .addAction(
                R.mipmap.ic_launcher,
                ActonButton,
                PendingIntent.getActivity(this,3,Acceptclick, FLAG_IMMUTABLE)
//                PendingIntent.getBroadcast(this, 1, intentAction, ints)
            )
            // adding action button
            .addAction(0, ActonButton2,
                PendingIntent.getActivity(this, 1, Declineclick, FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)
            .build()
    }

    /*
     * This method is called when the service should be started as foreground.
     */
    private fun notificationChannel() {
        // check if the version is equal or greater
        // than android oreo version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // creating notification channel and setting
            // the description of the channel
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NOTIFICATION_CHANNEL_DESC
            }
            // registering the channel to the System
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showForegroundServiceNotification() {
        if (mServiceNotification == null) {
            createServiceNotification()
        }
        startForeground(
            NOTIF_ID,
            mServiceNotification
        )
    }

    /*
     * This method is called when the service should be stopped as foreground.
     */
    override fun hideForegroundServiceNotification() {
        stopForeground(true)
    }

    fun startForeground() {
        org.linphone.core.tools.Log.i("[Core Service] Starting service as foreground")
        showForegroundServiceNotification()
        mIsInForegroundMode = true
        if (CoreManager.isReady()) {
//            CoreManager.instance().isServiceRunningAsForeground =
                mIsInForegroundMode
        }
    }

    fun stopForeground() {
        if (!mIsInForegroundMode) {
            org.linphone.core.tools.Log.w("[Core Service] Service isn't in foreground mode, nothing to do")
            return
        }
        org.linphone.core.tools.Log.i("[Core Service] Stopping service as foreground")
        hideForegroundServiceNotification()
        mIsInForegroundMode = false
        if (CoreManager.isReady()) {
//            CoreManager.instance().isServiceRunningAsForeground =
                mIsInForegroundMode
        }
    }

    private fun vibrate(caller: Address) {
        if (mVibrator != null && mVibrator!!.hasVibrator()) {
            mIsVibrating = if (mAudioManager!!.ringerMode == AudioManager.RINGER_MODE_SILENT) {
//                if (DeviceUtils.checkIfDoNotDisturbAllowsExceptionForFavoriteContacts(this)) {
////                                    boolean isContactFavorite = ;
//                    if (DeviceUtils.checkIfIsFavoriteContact(this, caller)) {
//                        org.linphone.core.tools.Log.i("[Core Service] Ringer mode is set to silent unless for favorite contact, which seems to be the case here, so starting vibrator")
//                        DeviceUtils.vibrate(mVibrator)
//                        true
//                    } else {
//                        false
////                        org.linphone.core.tools.Log.i("[Core Service] Do not vibrate as ringer mode is set to silent and calling username / SIP address isn't part of a favorite contact");
//                    }
//                } else {
                    true
//                                    Log.i("[Core Service] Do not vibrate as ringer mode is set to silent");
//                }
            } else {
                org.linphone.core.tools.Log.i("[Core Service] Starting vibrator")
                DeviceUtils.vibrate(mVibrator)
                true
            }
        } else {
            org.linphone.core.tools.Log.e("[Core Service] Device doesn't have a vibrator")
        }
    }
}
