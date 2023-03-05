package com.dggorbachev.voicenotes.base

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.dggorbachev.voicenotes.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Utils {

    fun getStringByDate(resources: Resources, millis: Long): String {
        val sdfDate = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))

        val now = Calendar.getInstance()
        val compared = Calendar.getInstance().apply { timeInMillis = millis }

        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.timeInMillis - millis)
        val hours = TimeUnit.MILLISECONDS.toHours(now.timeInMillis - millis)
        val days = TimeUnit.MILLISECONDS.toDays(now.timeInMillis - millis)

        if (minutes < 1)
            return resources.getString(R.string.just_now)
        if (minutes in 1L..59L)
            return getMinuteAddition(resources, minutes)
        if (hours in 1L..24L)
            return getHourAddition(resources, hours)
        if (days in 1L..30L)
            return getDaysAddition(resources, days)
        return resources.getString(
            R.string.date, sdfDate.format(millis), resources.getString(
                R.string.time_clock,
                compared.get(Calendar.HOUR_OF_DAY),
                compared.get(Calendar.MINUTE)
            )
        )
    }

    private fun getMinuteAddition(resources: Resources, minutes: Long): String {
        return when (minutes) {
            1L, 21L, 31L, 41L, 51L -> resources.getString(R.string.minute_accusative, minutes)
            2L, 3L, 4L, 22L, 23L, 24L, 32L, 33L, 34L, 42L, 43L, 44L, 52L, 53L, 54L -> resources.getString(
                R.string.minute_genitive,
                minutes
            )
            else -> resources.getString(R.string.minute_plural, minutes)

        }
    }

    private fun getHourAddition(resources: Resources, hours: Long): String {
        return when (hours) {
            1L, 21L -> resources.getString(R.string.hour_nominative, hours)
            2L, 3L, 4L, 22L, 23L -> resources.getString(
                R.string.hour_genitive,
                hours
            )
            else -> resources.getString(R.string.hour_plural, hours)

        }
    }

    private fun getDaysAddition(resources: Resources, days: Long): String {
        return when (days) {
            1L, 21L -> resources.getString(R.string.day_nominative, days)
            2L, 3L, 4L, 22L, 23L, 24L -> resources.getString(
                R.string.day_genitive,
                days
            )
            else -> resources.getString(R.string.day_plural, days)

        }
    }

    fun <T : Drawable> T.bytesEqualTo(t: T?) = toBitmap().bytesEqualTo(t?.toBitmap(), true)

    fun Bitmap.bytesEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false) =
        otherBitmap?.let { other ->
            if (width == other.width && height == other.height) {
                val res = toBytes().contentEquals(other.toBytes())
                if (shouldRecycle) {
                    doRecycle().also { otherBitmap.doRecycle() }
                }
                res
            } else false
        } ?: kotlin.run { false }


    private fun Bitmap.doRecycle() {
        if (!isRecycled) recycle()
    }

    private fun <T : Drawable> T.toBitmap(): Bitmap {
        if (this is BitmapDrawable) return bitmap

        val drawable: Drawable = this
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun Bitmap.toBytes(): ByteArray = ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.toByteArray()
    }
}