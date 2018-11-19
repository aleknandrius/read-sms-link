package com.example.andrius.smsreading

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


class MainActivity : Activity() {

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
        if (intent != null) {
            val url = intent.getStringExtra("url")
             if(url!=null){
                setUpWebView(url)
                 webView.loadUrl(url)
             } else {
                val webSettings = webView.settings
                webSettings.javaScriptEnabled = true
                webView.webViewClient = object: WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("Please, login to statymai.com if possible!")
                            .setPositiveButton("OK", null)
                            .show()
                        webView.webViewClient = object: WebViewClient(){
                        }
                    }
                }
                webView.loadUrl("https://www.statymai.com/")
            }
        }
    }

    private fun setUpWebView(url: String) {
        Log.d("ALKN", "setUpWebView")
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webView.setWebViewClient(client)
        webView.addJavascriptInterface(iface, "droid")
    }

    private var iface = JIFace()
    var client: WebViewClient = MyClient()


    internal inner class MyClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.d("ALKN", "onPageFinished")
            val ht = "javascript:window.droid.print(document.getElementsByTagName('html')[0].innerHTML);"
            webView.loadUrl(ht)
        }
    }
    internal inner class JIFace {
        @JavascriptInterface
        fun print(data: String) {
            Log.d("ALKN", "print")
            var data = data
            data = "<html>$data</html>"
            println(data)
            sendScreenshot(data)
        }
    }

    private fun sendScreenshot(html: String) {
        BackgroundMail.newBuilder(this@MainActivity)
            .withUsername("")
            .withPassword("")
            .withSenderName("Statymai.com")
            .withMailTo("")
            .withMailCc("")
            .withType(BackgroundMail.TYPE_PLAIN)
            .withSubject("Statymai.com")
            .withBody("Sms received: ${Date()} HTML:\n $html")
            .withOnSuccessCallback(object : BackgroundMail.OnSendingCallback {
                override fun onSuccess() {
                    Log.d("ALKN", "onSuccess")
                    finish()
                }

                override fun onFail(p0: Exception?) {
                    p0?.printStackTrace()
                    Log.d("ALKN", "onFail")
                    finish()
                }
            })
            .send()
    }


    private fun bitmapToFile(bitmap: Bitmap): String {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }
}
