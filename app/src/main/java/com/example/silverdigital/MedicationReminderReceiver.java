package com.example.silverdigital;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MedicationReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int medicamentoId = intent.getIntExtra("medicamentoId", -1);
        String nombreMedicamento = intent.getStringExtra("nombre");
        String tipoNotificacion = intent.getStringExtra("tipoNotificacion"); // "anticipada" o "principal"

        if (medicamentoId == -1 || nombreMedicamento == null || tipoNotificacion == null) {
            Log.e("MedicationReminder", "Datos inválidos para el medicamento.");
            return;
        }

        // Obtenemos SharedPreferences para verificar y actualizar el estado de las notificaciones
        SharedPreferences preferences = context.getSharedPreferences("MedicationPrefs", Context.MODE_PRIVATE);
        String keyAnticipada = "notificacion_anticipada_" + medicamentoId;

        if (tipoNotificacion.equals("anticipada")) {
            boolean yaNotificado = preferences.getBoolean(keyAnticipada, false);

            // Si ya se envió la notificación anticipada, no hacemos nada
            if (yaNotificado) {
                Log.d("MedicationReminder", "Notificación anticipada ya enviada para: " + nombreMedicamento);
                return;
            }

            // Marcar la notificación anticipada como enviada
            preferences.edit().putBoolean(keyAnticipada, true).apply();
        } else if (tipoNotificacion.equals("principal")) {
            // Reseteamos el estado para la notificación anticipada al enviar la notificación principal
            preferences.edit().remove(keyAnticipada).apply();
        }

        // Crear el texto de la notificación
        String contenidoTexto = tipoNotificacion.equals("anticipada")
                ? "En 2 minutos debes tomar tu medicina: " + nombreMedicamento
                : "Es hora de tomar tu medicina: " + nombreMedicamento;

        // Crear la intención para abrir la aplicación
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                medicamentoId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medication_reminder_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Recordatorio de Medicamento")
                .setContentText(contenidoTexto)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Enviar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(medicamentoId, builder.build());
        }

        // Logs para depuración
        Log.d("MedicationReminder", "Notificación enviada: " + contenidoTexto);
        if (tipoNotificacion.equals("anticipada")) {
            Log.d("MedicationReminder", "Estado actualizado para notificación anticipada: " + nombreMedicamento);
        } else {
            Log.d("MedicationReminder", "Estado reseteado tras notificación principal: " + nombreMedicamento);
        }
    }
}