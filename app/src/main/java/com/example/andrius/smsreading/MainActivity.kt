package com.example.andrius.smsreading

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
        if (intent != null) {
            val url = intent.getStringExtra("url")
            if (url != null) {
            } else {
                val webSettings = webView.settings
                webSettings.javaScriptEnabled = true
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("Please, login to statymai.com if possible!")
                            .setPositiveButton("OK", null)
                            .show()
                        webView.webViewClient = object : WebViewClient() {
                        }
                    }
                }
                webView.loadUrl("https://www.statymai.com/")
            }
        }
    }
}
