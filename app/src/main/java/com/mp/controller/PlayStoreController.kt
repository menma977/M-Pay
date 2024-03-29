package com.mp.controller

import android.os.AsyncTask
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException


class PlayStoreController {
    class GetVersion(private val packageName: String) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            return try {
                val newVersion =
                    Jsoup.connect("https://play.google.com/store/apps/details?id=$packageName&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText()
                JSONObject("{Status: 0, Version: $newVersion}")
            } catch (e: IOException) {
                e.printStackTrace()
                JSONObject("{Status: 1, Massage: 'internet tidak setabil'}")
            }
        }
    }
}