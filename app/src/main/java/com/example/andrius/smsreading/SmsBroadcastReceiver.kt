package com.example.andrius.smsreading

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<Any>
                val messages = arrayOfNulls<SmsMessage>(pdus.size)
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                }
                if (messages.isNotEmpty() && messages[0]!!.messageBody.toLowerCase().contains("statymai.com")) {
                    Log.d("ALKN", "onReceive")
                    val serviceIntent = Intent(context, MainActivity::class.java)
                    val messageBody = messages[0]!!.messageBody
                    if (messageBody != null) {
                        val url = parseLink(messageBody)
                        if (url != null) {
                            serviceIntent.putExtra("url", messageBody)
                            context.startActivity(serviceIntent)
                        }
                    }
                }
            }
        }
    }

    private fun parseLink(message: String): String? {
        val splited = message.split("\\s+")
        return splited.find { word ->
            (word.contains("http") || word.contains("wwww")) && word.contains("statymai.com")
        }
    }

    companion object {
        private val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }
}
