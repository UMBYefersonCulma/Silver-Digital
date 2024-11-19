package com.example.silverdigital.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.MedicamentoFormActivity;
import com.example.silverdigital.R;
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
        Context context = holder.itemView.getContext(); // Obtener el contexto para lanzar actividades

        // Mostrar y configurar Medicamento 1 (siempre está presente)
        Medicamento medicamento1 = grupo.get(0);
        setMedicamentoData(holder.tvNombreMedicamento1, holder.tvDosisMedicamento1, holder.tvHorarioMedicamento1, holder.tvDosisMedicamento1,medicamento1);
        setOnClickListener(holder.itemView, context, medicamento1);

        // Mostrar y configurar Medicamento 2 (si existe)
        if (grupo.size() > 1) {
            Medicamento medicamento2 = grupo.get(1);
            setMedicamentoData(holder.tvNombreMedicamento2, holder.tvDosisMedicamento2, holder.tvHorarioMedicamento2, holder.tvDosisMedicamento2,medicamento2);
            holder.linearLayoutMedicamento2.setVisibility(View.VISIBLE);
            setOnClickListener(holder.linearLayoutMedicamento2, context, medicamento2);
        } else {
            holder.linearLayoutMedicamento2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return gruposMedicamentos.size();
    }

    /**
     * Actualizar los datos del adaptador
     */
    public void updateData(List<List<Medicamento>> grupos) {
        this.gruposMedicamentos.clear(); // Limpiar la lista actual
        this.gruposMedicamentos.addAll(grupos); // Agregar los nuevos grupos
        notifyDataSetChanged(); // Notificar cambios al adaptador
    }

    /**
     * Configurar los datos del medicamento en las vistas
     */
    private void setMedicamentoData(TextView tvNombre, TextView tvDosis, TextView tvHorario, TextView tvObservaciones, Medicamento medicamento) {
        tvNombre.setText(medicamento.getNombre() != null ? medicamento.getNombre() : "Nombre no disponible");
        tvDosis.setText(medicamento.getDosis() != null ? "Dosis: " + medicamento.getDosis() : "Dosis no disponible");
        tvHorario.setText(medicamento.getHorario() != null ? "Horario: " + medicamento.getHorario() : "Horario no disponible");
        tvObservaciones.setText(medicamento.getObservaciones() != null ? "Observaciones: " + medicamento.getObservaciones() : "Observaciones no disponibles");
    }

    /**
     * Configurar el listener para abrir la actividad de edición del medicamento
     */
    private void setOnClickListener(View view, Context context, Medicamento medicamento) {
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
     * ViewHolder para los medicamentos
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Vistas para el primer medicamento
        TextView tvNombreMedicamento1, tvDosisMedicamento1, tvHorarioMedicamento1 ,tvObservaciones1;

        // Vistas para el segundo medicamento
        TextView tvNombreMedicamento2, tvDosisMedicamento2, tvHorarioMedicamento2 ,tvObservaciones2;
        View linearLayoutMedicamento2; // Contenedor para el segundo medicamento

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asociar vistas del primer medicamento
            tvNombreMedicamento1 = itemView.findViewById(R.id.tvNombreMedicamento1);
            tvDosisMedicamento1 = itemView.findViewById(R.id.tvDosisMedicamento1);
            tvHorarioMedicamento1 = itemView.findViewById(R.id.tvHorarioMedicamento1);
            tvObservaciones1 = itemView.findViewById(R.id.tvObservaciones1);

            // Asociar vistas del segundo medicamento
            tvNombreMedicamento2 = itemView.findViewById(R.id.tvNombreMedicamento2);
            tvDosisMedicamento2 = itemView.findViewById(R.id.tvDosisMedicamento2);
            tvHorarioMedicamento2 = itemView.findViewById(R.id.tvHorarioMedicamento2);
            tvObservaciones2 = itemView.findViewById(R.id.tvObservaciones2);
            linearLayoutMedicamento2 = itemView.findViewById(R.id.linearLayoutMedicamento2);
        }
    }
}