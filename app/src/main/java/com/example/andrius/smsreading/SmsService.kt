package com.example.andrius.smsreading

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import org.jsoup.Jsoup
import java.util.*

class SmsService : Service() {

    private lateinit var webView: WebView
    private var iface = JIFace()
    var client: WebViewClient = MyClient()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onHandleIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val url = intent.getStringExtra("url")
            if (url != null) {
                setUpWebView()
                webView.loadUrl(url)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = client
        webView.addJavascriptInterface(iface, "droid")
    }

    internal inner class MyClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            val ht = "javascript:window.droid.print(document.getElementsByTagName('html')[0].innerHTML);"
            webView.loadUrl(ht)
        }
    }

    internal inner class JIFace {
        @JavascriptInterface
        fun print(data: String) {
            val doc = Jsoup.parse(data)
            val elements = doc.getElementsByClass("bet")
            if (elements.isNotEmpty()) {
                val paidElement = elements.find { it.hasClass("paid") }
                sendEmail(paidElement.toString())
            }
        }
    }

    private fun sendEmail(html: String) {
        BackgroundMail.newBuilder(this)
            .withUsername("@gmail.com")
            .withPassword("")
            .withSenderName("Statymai.com")
            .withMailTo("@gmail.com")
            .withMailCc("@gmail.com")
            .withType(BackgroundMail.TYPE_HTML)
            .withSubject("Statymai.com")
            .withBody("Sms received: ${Date()} HTML:\n $html")
            .withOnSuccessCallback(object : BackgroundMail.OnSendingCallback {
                override fun onSuccess() {
                    stopSelf()
                }

                override fun onFail(p0: Exception?) {
                    p0?.printStackTrace()
                    stopSelf()
                }
            })
            .send()
    }

    override fun onCreate() {
        super.onCreate()
        webView = WebView(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
