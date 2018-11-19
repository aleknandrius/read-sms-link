package com.example.andrius.smsreading

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.regex.Pattern
import org.jsoup.Jsoup
import org.jsoup.Connection
import java.util.concurrent.Executors


class SmsService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId)
    }

    // will be called asynchronously by Android
    private fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val message = intent.getStringExtra("message")
            val url = parseLink(message)
            if (url != null) {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    //openPage("https://www.statymai.com/tipster/vamos--")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
            }
        }
    }

    private fun openPage(url: String) {
        val username = "Macke"
        val password = "macke"

        val cookies = HashMap<String, String>()
        val formData = HashMap<String, String>()
        val loginForm = Jsoup.connect(homeUrl)
            .method(Connection.Method.GET)
            .userAgent(System.getProperty("http.agent"))
            .execute()

        if (needLogin(loginForm.body())) {
            cookies.putAll(loginForm.cookies())

            formData.put("email_username", username);
            formData.put("password", password);

            val homePage = Jsoup.connect(loginUrl)
                .cookies(cookies)
                .data(formData)
                .method(Connection.Method.POST)
                .userAgent(System.getProperty("http.agent"))
                .execute()
            cookies.clear()
            cookies.putAll(homePage.cookies())
        }
//        try {
//            val sourceDoc = Jsoup.connect(url)
//                .cookies(cookies)
//                .method(Connection.Method.GET)
//                .userAgent(System.getProperty("http.agent"))
//                .execute()
//            val html = sourceDoc.parse().html()
//            Log.d("SMS", "html: " + html)
//        } catch ( e: Exception){
//            Log.e("SMS", "parsing", e)
//        }

        try {
            val sourceDoc = Jsoup.connect("https://www.statymai.com/payment/list")
                .cookies(cookies)
                .method(Connection.Method.GET)
                .userAgent(System.getProperty("http.agent"))
                .execute()
            val html = sourceDoc.parse().html()
            Log.d("SMS", "html: " + html)
        } catch ( e: Exception){
            Log.e("SMS", "parsing", e)
        }
    }

    private fun needLogin(html: String): Boolean {
        return html.contains("icon-sign-in")
    }

    private fun parseLink(message: String): String? {
        val splited = message.split("\\s+")
        return splited.find { word ->
            (word.contains("http") || word.contains("wwww")) && word.contains("statymai.com")
        }
        /*val links = mutableListOf<String>()

        val regex = "\\(?\\b(http://|www|[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        val p = Pattern.compile(regex);
        val m = p.matcher(message);
        while(m.find()) {
            var urlStr = m.group()
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.count() - 1);
            }
            links.add(urlStr);
        }
        return links.firstOrNull()*/
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    companion object {
        private val userAgent = System.getProperty("http.agent")
        private const val homeUrl = "https://statymai.com/?mobile=1"
        private const val loginUrl = "https://statymai.com/login"

    }
}
