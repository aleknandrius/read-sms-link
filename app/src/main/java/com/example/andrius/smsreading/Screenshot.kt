package com.example.andrius.smsreading

import android.graphics.Bitmap
import android.view.View


class Screenshot {
    companion object {
        fun takeScreenshot(v: View): Bitmap {
            v.setDrawingCacheEnabled(true)
            v.buildDrawingCache(true)
            val b = Bitmap.createBitmap(v.getDrawingCache())
            v.setDrawingCacheEnabled(false)
            return b
        }

        fun takeScreenshotOfRootView(v: View): Bitmap {
            return takeScreenshot(v.getRootView())
        }

    }

}