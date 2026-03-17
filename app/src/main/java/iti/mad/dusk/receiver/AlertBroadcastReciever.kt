package iti.mad.dusk.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import iti.mad.dusk.core.alert.AlertScheduler
import iti.mad.dusk.domain.model.AlertType
import iti.mad.dusk.domain.repo.AlertRepository
import iti.mad.dusk.service.WeatherAlertService
import iti.mad.dusk.worker.NotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlertBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var alertRepository: AlertRepository
    @Inject lateinit var alertScheduler: AlertScheduler

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.LOCKED_BOOT_COMPLETED" -> handleBoot()
            else -> handleAlarmFired(context, intent)
        }
    }

    private fun handleBoot() {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alerts = alertRepository.getAllAlerts().firstOrNull() ?: return@launch
                alerts.filter { it.isActive }.forEach { alert ->
                    alertScheduler.schedule(alert)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleAlarmFired(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra(AlertScheduler.EXTRA_ALERT_ID, -1)
        val alertType = intent.getStringExtra(AlertScheduler.EXTRA_ALERT_TYPE) ?: return
        val fromHour = intent.getIntExtra(AlertScheduler.EXTRA_ALERT_FROM_HOUR, -1)
        val fromMinute = intent.getIntExtra(AlertScheduler.EXTRA_ALERT_FROM_MINUTE, -1)
        val toHour = intent.getIntExtra(AlertScheduler.EXTRA_ALERT_TO_HOUR, -1)
        val toMinute = intent.getIntExtra(AlertScheduler.EXTRA_ALERT_TO_MINUTE, -1)

        if (alertId == -1 || fromHour == -1 || fromMinute == -1) return

        dispatchAlert(context, alertId, alertType, fromHour, fromMinute, toHour, toMinute)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alerts = alertRepository.getAllAlerts().firstOrNull() ?: return@launch
                val alert = alerts.find { it.id == alertId.toLong() && it.isActive } ?: return@launch
                alertScheduler.scheduleNextOccurrence(alert)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun dispatchAlert(
        context: Context,
        alertId: Int,
        alertType: String,
        fromHour: Int,
        fromMinute: Int,
        toHour: Int,
        toMinute: Int,
    ) {
        when (AlertType.valueOf(alertType)) {
            AlertType.NOTIFICATION -> {
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(
                        workDataOf(
                            NotificationWorker.KEY_ALERT_ID to alertId,
                            NotificationWorker.KEY_FROM_HOUR to fromHour,
                            NotificationWorker.KEY_FROM_MINUTE to fromMinute,
                            NotificationWorker.KEY_TO_HOUR to toHour,
                            NotificationWorker.KEY_TO_MINUTE to toMinute,
                        )
                    )
                    .build()
                WorkManager.getInstance(context).enqueue(workRequest)
            }
            AlertType.ALARM -> {
                val serviceIntent = Intent(context, WeatherAlertService::class.java).apply {
                    putExtra(AlertScheduler.EXTRA_ALERT_ID, alertId)
                    putExtra(AlertScheduler.EXTRA_ALERT_FROM_HOUR, fromHour)
                    putExtra(AlertScheduler.EXTRA_ALERT_FROM_MINUTE, fromMinute)
                    putExtra(AlertScheduler.EXTRA_ALERT_TO_HOUR, toHour)
                    putExtra(AlertScheduler.EXTRA_ALERT_TO_MINUTE, toMinute)
                }
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}