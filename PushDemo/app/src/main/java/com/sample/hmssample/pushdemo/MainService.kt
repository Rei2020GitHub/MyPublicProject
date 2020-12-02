package com.sample.hmssample.pushdemo

import android.content.Intent
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import java.lang.Exception

class MainService: HmsMessageService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        val intent = Intent().apply {
            action = Const.ACTION
            putExtra(Const.KEY_COMMEND, Const.COMMEND_NEW_TOKEN)
            putExtra(Const.KEY_TOKEN, token)
        }
        sendBroadcast(intent)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        val intent = Intent().apply {
            action = Const.ACTION
            putExtra(Const.KEY_COMMEND, Const.COMMEND_MESSAGE_RECEIVED)
            putExtra(Const.KEY_REMOTE_MESSAGE, message)
        }
        sendBroadcast(intent)
    }

    override fun onTokenError(exception: Exception?) {
        super.onTokenError(exception)
        val intent = Intent().apply {
            action = Const.ACTION
            putExtra(Const.KEY_COMMEND, Const.COMMEND_TOKEN_ERROR)
        }
        sendBroadcast(intent)
    }
}