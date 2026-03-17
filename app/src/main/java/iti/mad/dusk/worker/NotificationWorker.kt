package iti.mad.dusk.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import iti.mad.dusk.R
import iti.mad.dusk.core.alert.SeverityThresholds
import iti.mad.dusk.core.base.Resource
import iti.mad.dusk.domain.exception.WeatherException
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.repo.LocationRepository
import iti.mad.dusk.domain.repo.WeatherRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_ALERT_ID = "ALERT_ID"
        const val KEY_FROM_HOUR = "FROM_HOUR"
        const val KEY_FROM_MINUTE = "FROM_MINUTE"
        const val KEY_TO_HOUR = "TO_HOUR"
        const val KEY_TO_MINUTE = "TO_MINUTE"
        private const val CHANNEL_ID = "WEATHER_ALERT_CHANNEL"
        private const val DEFAULT_NOTIF_ID = 2000
    }

    override suspend fun doWork(): Result {
        Log.d("NotificationWorker", "doWork started")
        createNotificationChannel()

        val alertId = inputData.getInt(KEY_ALERT_ID, DEFAULT_NOTIF_ID)
        val fromHour = inputData.getInt(KEY_FROM_HOUR, 0)
        val fromMinute = inputData.getInt(KEY_FROM_MINUTE, 0)
        val toHour = inputData.getInt(KEY_TO_HOUR, 0)
        val toMinute = inputData.getInt(KEY_TO_MINUTE, 0)

        return runCatching {
            Log.d("NotificationWorker", "building weather strings")
            val (title, body, expanded) = buildWeatherStrings(
                fromHour, fromMinute, toHour, toMinute
            )
            Log.d("NotificationWorker", "showing notification: $title / $body")
            showNotification(alertId, title, body, expanded)
            Log.d("NotificationWorker", "notification shown ✅")
            Result.success()
        }.getOrElse { exception ->
            Log.e("NotificationWorker", "doWork failed ❌", exception)
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }

    private suspend fun buildWeatherStrings(
        fromHour: Int,
        fromMinute: Int,
        toHour: Int,
        toMinute: Int,
    ): Triple<String, String, String> {
        return runCatching {
            val default =
                locationRepository.getDefaultLocation().firstOrNull() ?: return fallbackStrings()

            val weather = weatherRepository.getCurrentWeather(
                lat = default.lat,
                lon = default.lon,
                forceFresh = false,
            ).firstOrNull() ?: return fallbackStrings()

            val forecast = weatherRepository.getForecast(
                lat = default.lat,
                lon = default.lon,
                forceFresh = false,
            ).firstOrNull()

            if (forecast == null || forecast !is Resource.Success || weather !is Resource.Success) {
                throw WeatherException.Unknown("Couldn't fetch weather data")
            }

            val windowFrom = fromHour * 60 + fromMinute
            val windowTo = toHour * 60 + toMinute

            val isSevere = forecast.data.hourly.any { hourly ->
                val cal = Calendar.getInstance().apply { timeInMillis = hourly.timestamp * 1000L }
                val slotMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
                slotMinutes in windowFrom..windowTo && SeverityThresholds.isSevere(
                    conditionCode = hourly.condition.id,
                    windSpeedMs = hourly.wind.speed.toDouble(),
                    tempCelsius = hourly.temperature.current.toDouble(),
                )
            }

            val severityLine = if (isSevere) "⚠️ Severe weather in your alert window\n"
            else "✅ No severe weather in your alert window\n"

            Triple(
                "Weather · ${default.cityName}",
                "${weather.data.temperature.current.toInt()}°",
                buildExpandedText(weather.data, severityLine),
            )
        }.getOrElse { fallbackStrings() }
    }

    private fun fallbackStrings() = Triple(
        "Weather alert",
        "Check the latest weather conditions.",
        "Open Dusk to see your current forecast.",
    )

    private fun buildExpandedText(weather: CurrentWeather, severityLine: String): String =
        severityLine + "${weather.temperature.current.toInt()}°  Feels like ${weather.temperature.feelsLike.toInt()}°\n" + "↑ ${weather.temperature.max.toInt()}°  ↓ ${weather.temperature.min.toInt()}° · " + "💧 ${weather.temperature.humidity}%  💨 ${weather.wind.speed} m/s"

    private fun showNotification(id: Int, title: String, text: String, expandedText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title)
            .setContentText(text).setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setAutoCancel(true).build()

        NotificationManagerCompat.from(context).notify(id, notif)
    }

    private fun createNotificationChannel() {
        val ch = NotificationChannel(
            CHANNEL_ID, "Weather alerts", NotificationManager.IMPORTANCE_HIGH
        )
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            ch
        )
    }
}