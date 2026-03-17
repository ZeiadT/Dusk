package iti.mad.dusk.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import iti.mad.dusk.R
import iti.mad.dusk.core.alert.AlertScheduler
import iti.mad.dusk.core.alert.SeverityThresholds
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.repo.LocationRepository
import iti.mad.dusk.domain.repo.WeatherRepository
import iti.mad.dusk.app.MainActivity
import iti.mad.dusk.core.base.Resource
import iti.mad.dusk.domain.exception.WeatherException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class WeatherAlertService : Service() {

    @Inject
    lateinit var locationRepository: LocationRepository
    @Inject
    lateinit var weatherRepository: WeatherRepository

    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_STOP_ALARM = "iti.mad.dusk.ACTION_STOP_ALARM"
        private const val CHANNEL_ID = "ALARM_SERVICE_CHANNEL"
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_STOP = 1
        private const val TITLE_LOADING = "Weather Alarm"
        private const val TEXT_LOADING = "Checking current weather…"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_ALARM) {
            stopAlarmAndSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        val fromHour = intent?.getIntExtra(AlertScheduler.EXTRA_ALERT_FROM_HOUR, 0) ?: 0
        val fromMinute = intent?.getIntExtra(AlertScheduler.EXTRA_ALERT_FROM_MINUTE, 0) ?: 0
        val toHour = intent?.getIntExtra(AlertScheduler.EXTRA_ALERT_TO_HOUR, 0) ?: 0
        val toMinute = intent?.getIntExtra(AlertScheduler.EXTRA_ALERT_TO_MINUTE, 0) ?: 0

        val stopPi = buildStopPendingIntent()
        startForeground(
            NOTIFICATION_ID,
            buildNotification(TITLE_LOADING, TEXT_LOADING, TEXT_LOADING, stopPi)
        )

        startAlarmAudio()
        fetchWeatherThenUpdateNotification(stopPi, fromHour, fromMinute, toHour, toMinute)

        return START_STICKY
    }

    private fun startAlarmAudio() {
        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(applicationContext, Settings.System.DEFAULT_ALARM_ALERT_URI)
                isLooping = true
                prepare()
                start()
            }
        }
    }

    private fun stopAlarmAndSelf() {
        runCatching { mediaPlayer?.stop(); mediaPlayer?.release() }
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun fetchWeatherThenUpdateNotification(
        stopPi: PendingIntent,
        fromHour: Int,
        fromMinute: Int,
        toHour: Int,
        toMinute: Int,
    ) {
        serviceScope.launch {
            runCatching {
                val (title, body, expanded) = buildWeatherStrings(
                    fromHour,
                    fromMinute,
                    toHour,
                    toMinute
                )
                val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIFICATION_ID, buildNotification(title, body, expanded, stopPi))
            }
        }
    }

    private suspend fun buildWeatherStrings(
        fromHour: Int,
        fromMinute: Int,
        toHour: Int,
        toMinute: Int,
    ): Triple<String, String, String> {
        return runCatching {
            val default = locationRepository.getDefaultLocation().firstOrNull()
                ?: return fallbackStrings()

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

            val severityLine =
                if (isSevere) "⚠️ Severe weather in your alert window\n" else "✅ No severe weather in your alert window\n"

            Triple(
                "Weather alarm · ${default.cityName}",
                "${weather.data.temperature.current.toInt()}°",
                buildExpandedText(weather.data, severityLine),
            )
        }.getOrElse { fallbackStrings() }
    }

    private fun fallbackStrings() = Triple(
        "Weather alarm",
        "Time to check the forecast",
        "Open Dusk to see your current weather.",
    )

    private fun buildExpandedText(weather: CurrentWeather, severityLine: String): String =
        severityLine +
                "${weather.temperature.current.toInt()}°  Feels like ${weather.temperature.feelsLike.toInt()}°\n" +
                "↑ ${weather.temperature.max.toInt()}°  ↓ ${weather.temperature.min.toInt()}° · " +
                "💧 ${weather.temperature.humidity}%  💨 ${weather.wind.speed} m/s"

    private fun buildStopPendingIntent(): PendingIntent =
        PendingIntent.getService(
            this,
            REQUEST_STOP,
            Intent(this, WeatherAlertService::class.java).apply { action = ACTION_STOP_ALARM },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    private fun buildNotification(
        title: String,
        text: String,
        expandedText: String,
        stopPi: PendingIntent,
    ): Notification {
        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPi = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(R.drawable.ic_launcher_background, "Stop alarm", stopPi)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(fullScreenPi)
            .setFullScreenIntent(fullScreenPi, true)
            .build()
    }

    private fun createNotificationChannel() {
        val ch = NotificationChannel(
            CHANNEL_ID, "Weather alarms", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setBypassDnd(true)
            enableVibration(true)
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(ch)
    }

    override fun onDestroy() {
        runCatching { mediaPlayer?.stop(); mediaPlayer?.release() }
        serviceScope.cancel()
        super.onDestroy()
    }
}