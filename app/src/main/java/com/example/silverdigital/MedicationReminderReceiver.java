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
        boolean esAnticipado = intent.getBooleanExtra("esAnticipado", false);

        if (medicamentoId == -1 || nombreMedicamento == null) {
            Log.e("MedicationReminder", "Datos inválidos para el medicamento.");
            return;
        }

        // Obtenemos SharedPreferences para verificar y actualizar el estado de las notificaciones
        SharedPreferences preferences = context.getSharedPreferences("MedicationPrefs", Context.MODE_PRIVATE);
        String key = "notificacion_" + medicamentoId;

        boolean yaNotificado = preferences.getBoolean(key, false);

        // Si es una notificación anticipada y ya se envió, no hacemos nada
        if (esAnticipado && yaNotificado) {
            Log.d("MedicationReminder", "Notificación anticipada ya enviada para: " + nombreMedicamento);
            return;
        }
        boolean tes = false;
        // Crear el texto de la notificación
        String contenidoTexto = esAnticipado
                ? "Pronto debes tomar tu medicina: " + nombreMedicamento
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

        // Actualizamos el estado en SharedPreferences si es una notificación anticipada
        if (esAnticipado) {
            preferences.edit().putBoolean(key, true).apply();
            Log.d("MedicationReminder", "Estado actualizado para notificación anticipada: " + nombreMedicamento);
        } else {
            // Si es la notificación exacta, reiniciamos el estado para futuras alarmas
            preferences.edit().remove(key).apply();
            Log.d("MedicationReminder", "Estado reseteado tras notificación exacta: " + nombreMedicamento);
        }
    }
}