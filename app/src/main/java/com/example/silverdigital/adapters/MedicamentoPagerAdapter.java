package com.example.silverdigital.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silverdigital.MedicamentoFormActivity;
import com.example.silverdigital.MedicationReminderReceiver;
import com.example.silverdigital.R;
import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.model.Medicamento;

import java.util.List;

public class MedicamentoPagerAdapter extends RecyclerView.Adapter<MedicamentoPagerAdapter.ViewHolder> {

    private final List<List<Medicamento>> gruposMedicamentos;

    public MedicamentoPagerAdapter(List<List<Medicamento>> gruposMedicamentos) {
        this.gruposMedicamentos = gruposMedicamentos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicamento_item_vertical, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Medicamento> grupo = gruposMedicamentos.get(position);
        Context context = holder.itemView.getContext();

        if (grupo == null || grupo.isEmpty()) {
            Log.e("MedicamentoPagerAdapter", "El grupo de medicamentos está vacío o es nulo.");
            return;
        }

        // Configurar Medicamento 1
        Medicamento medicamento1 = grupo.get(0);
        setMedicamentoData(holder.tvNombreMedicamento1, holder.tvDosisMedicamento1, holder.tvHorarioMedicamento1, holder.tvObservaciones1, medicamento1);
        setOnClickListener(holder.itemView, context, medicamento1);

        // Configurar Medicamento 2 (si existe)
        if (grupo.size() > 1) {
            Medicamento medicamento2 = grupo.get(1);
            setMedicamentoData(holder.tvNombreMedicamento2, holder.tvDosisMedicamento2, holder.tvHorarioMedicamento2, holder.tvObservaciones2, medicamento2);
            if (holder.linearLayoutMedicamento2 != null) {
                holder.linearLayoutMedicamento2.setVisibility(View.VISIBLE);
                setOnClickListener(holder.linearLayoutMedicamento2, context, medicamento2);
            }
        } else if (holder.linearLayoutMedicamento2 != null) {
            holder.linearLayoutMedicamento2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return gruposMedicamentos != null ? gruposMedicamentos.size() : 0;
    }

    /**
     * Actualizar los datos del adaptador.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<List<Medicamento>> grupos) {
        this.gruposMedicamentos.clear();
        if (grupos != null) {
            this.gruposMedicamentos.addAll(grupos);
        }
        notifyDataSetChanged();
    }

    /**
     * Configurar los datos del medicamento en las vistas.
     */
    private void setMedicamentoData(TextView tvNombre, TextView tvDosis, TextView tvHorario, TextView tvObservaciones, Medicamento medicamento) {
        if (medicamento == null) {
            Log.e("MedicamentoPagerAdapter", "El medicamento es nulo.");
            return;
        }

        if (tvNombre != null) {
            tvNombre.setText(medicamento.getNombre() != null ? medicamento.getNombre() : "Nombre no disponible");
        }

        if (tvDosis != null) {
            tvDosis.setText(medicamento.getDosis() != null ? "Dosis: " + medicamento.getDosis() : "Dosis no disponible");
        }

        if (tvHorario != null) {
            tvHorario.setText(medicamento.getHorario() != null ? "Horario: " + medicamento.getHorario() : "Horario no disponible");
        }

        if (tvObservaciones != null) {
            tvObservaciones.setText(medicamento.getObservaciones() != null ? "Observaciones: " + medicamento.getObservaciones() : "Observaciones no disponibles");
        }
    }

    /**
     * Configurar el listener para abrir la actividad de edición del medicamento.
     */
    private void setOnClickListener(View view, Context context, Medicamento medicamento) {
        if (view == null || medicamento == null) return;

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, MedicamentoFormActivity.class);
            intent.putExtra("medicamentoId", medicamento.getId());
            intent.putExtra("nombre", medicamento.getNombre());
            intent.putExtra("dosis", medicamento.getDosis());
            intent.putExtra("horario", medicamento.getHorario());
            intent.putExtra("observaciones", medicamento.getObservaciones());
            context.startActivity(intent);
        });
    }

    /**
     * Eliminar un medicamento de la base de datos y cancelar su notificación.
     */
    private void eliminarMedicamento(Context context, Medicamento medicamento, int position) {
        AppDatabase db = AppDatabase.getDatabase(context);
        new Thread(() -> {
            if (medicamento != null) {
                db.medicamentoDao().delete(medicamento);

                // Cancelar notificación asociada
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, MedicationReminderReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        medicamento.getId(),
                        intent,
                        PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
                );
                if (alarmManager != null && pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                }

                ((Activity) context).runOnUiThread(() -> {
                    gruposMedicamentos.remove(position);
                    notifyDataSetChanged();
                });
            }
        }).start();
    }

    /**
     * ViewHolder para los medicamentos.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreMedicamento1, tvDosisMedicamento1, tvHorarioMedicamento1, tvObservaciones1;
        TextView tvNombreMedicamento2, tvDosisMedicamento2, tvHorarioMedicamento2, tvObservaciones2;
        View linearLayoutMedicamento2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreMedicamento1 = itemView.findViewById(R.id.tvNombreMedicamento1);
            tvDosisMedicamento1 = itemView.findViewById(R.id.tvDosisMedicamento1);
            tvHorarioMedicamento1 = itemView.findViewById(R.id.tvHorarioMedicamento1);
            tvObservaciones1 = itemView.findViewById(R.id.tvObservaciones1);

            tvNombreMedicamento2 = itemView.findViewById(R.id.tvNombreMedicamento2);
            tvDosisMedicamento2 = itemView.findViewById(R.id.tvDosisMedicamento2);
            tvHorarioMedicamento2 = itemView.findViewById(R.id.tvHorarioMedicamento2);
            tvObservaciones2 = itemView.findViewById(R.id.tvObservaciones2);
            linearLayoutMedicamento2 = itemView.findViewById(R.id.linearLayoutMedicamento2);
        }
    }
}