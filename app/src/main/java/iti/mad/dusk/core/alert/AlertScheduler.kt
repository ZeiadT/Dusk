package iti.mad.dusk.core.alert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import iti.mad.dusk.domain.model.WeatherAlert
import iti.mad.dusk.receiver.AlertBroadcastReceiver
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val EXTRA_ALERT_ID = "ALERT_ID"
        const val EXTRA_ALERT_TYPE = "ALERT_TYPE"
        const val EXTRA_ALERT_FROM_HOUR = "ALERT_FROM_HOUR"
        const val EXTRA_ALERT_FROM_MINUTE = "ALERT_FROM_MINUTE"
        const val EXTRA_ALERT_TO_HOUR = "ALERT_TO_HOUR"
        const val EXTRA_ALERT_TO_MINUTE = "ALERT_TO_MINUTE"
        private const val TRIGGER_OFFSET_MINUTES = 60
    }

    fun canScheduleExactAlarms(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            alarmManager.canScheduleExactAlarms()
        else true

    fun schedule(alert: WeatherAlert) {
        if (!canScheduleExactAlarms()) return
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            buildTriggerTime(alert.fromHour, alert.fromMinute, skipToday = false),
            buildPendingIntent(alert),
        )
    }

    fun scheduleNextOccurrence(alert: WeatherAlert) {
        if (!canScheduleExactAlarms()) return
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            buildTriggerTime(alert.fromHour, alert.fromMinute, skipToday = true),
            buildPendingIntent(alert),
        )
    }

    fun cancel(alertId: Long) {
        val intent = Intent(context, AlertBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun buildTriggerTime(hour: Int, minute: Int, skipToday: Boolean): Long {
        val now = Calendar.getInstance()
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -TRIGGER_OFFSET_MINUTES)
        }
        if (skipToday || !trigger.after(now)) {
            trigger.add(Calendar.DATE, 1)
        }
        return trigger.timeInMillis
    }

    private fun buildPendingIntent(alert: WeatherAlert): PendingIntent {
        val intent = Intent(context, AlertBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_ALERT_ID, alert.id.toInt())
            putExtra(EXTRA_ALERT_TYPE, alert.type.name)
            putExtra(EXTRA_ALERT_FROM_HOUR, alert.fromHour)
            putExtra(EXTRA_ALERT_FROM_MINUTE, alert.fromMinute)
            putExtra(EXTRA_ALERT_TO_HOUR, alert.toHour)
            putExtra(EXTRA_ALERT_TO_MINUTE, alert.toMinute)
        }
        return PendingIntent.getBroadcast(
            context,
            alert.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}