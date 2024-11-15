package com.example.silverdigital;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class MedicationReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int medicamentoId = intent.getIntExtra("medicamentoId", -1);
        String nombreMedicamento = intent.getStringExtra("nombre");
        boolean esAnticipado = intent.getBooleanExtra("esAnticipado", false); // Obtener el valor de esAnticipado

        Log.d("MedicationReminder", "Notificación recibida para: " + nombreMedicamento);

        // Crear el texto de la notificación con el condicional para verificar si es una notificación anticipada
        String contenidoTexto;
        assert nombreMedicamento != null;
        if (nombreMedicamento.contains("Pronto debes tomar:")) {
            contenidoTexto = "2 minutos para tomar: " + nombreMedicamento.replace("Pronto debes tomar: ", "");
        } else {
            contenidoTexto = "Es hora de tomar: " + nombreMedicamento;
        }

        // Crear la intención para abrir la aplicación
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                medicamentoId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación con el contenido actualizado
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medication_reminder_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Recordatorio de Medicamento")
                .setContentText(contenidoTexto) // Usar contenidoTexto aquí
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(medicamentoId, builder.build());
    }
}