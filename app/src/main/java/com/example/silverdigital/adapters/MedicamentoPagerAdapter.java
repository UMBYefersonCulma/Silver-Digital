package com.example.silverdigital.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
        Context context = holder.itemView.getContext();

        // Configurar Medicamento 1
        if (grupo.size() > 0) {
            Medicamento medicamento1 = grupo.get(0);
            setMedicamentoData(
                    holder.tvNombreMedicamento1,
                    holder.tvDosisMedicamento1,
                    holder.tvHorarioMedicamento1,
                    holder.tvFrecuenciaMedicamento1,
                    holder.tvDiasSemanaMedicamento1,
                    holder.tvObservaciones1,
                    medicamento1
            );

            holder.linearLayoutMedicamento1.setVisibility(View.VISIBLE);
            holder.linearLayoutMedicamento1.setOnClickListener(v -> abrirMenuEdicion(context, medicamento1));
        } else {
            holder.linearLayoutMedicamento1.setVisibility(View.GONE);
        }

        // Configurar Medicamento 2 (si existe)
        if (grupo.size() > 1) {
            Medicamento medicamento2 = grupo.get(1);
            setMedicamentoData(
                    holder.tvNombreMedicamento2,
                    holder.tvDosisMedicamento2,
                    holder.tvHorarioMedicamento2,
                    holder.tvFrecuenciaMedicamento2,
                    holder.tvDiasSemanaMedicamento2,
                    holder.tvObservaciones2,
                    medicamento2
            );

            holder.linearLayoutMedicamento2.setVisibility(View.VISIBLE);
            holder.linearLayoutMedicamento2.setOnClickListener(v -> abrirMenuEdicion(context, medicamento2));
        } else {
            holder.linearLayoutMedicamento2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return gruposMedicamentos != null ? gruposMedicamentos.size() : 0;
    }

    /**
     * Abrir el menú de edición con los datos del medicamento.
     */
    private void abrirMenuEdicion(Context context, Medicamento medicamento) {
        Intent intent = new Intent(context, MedicamentoFormActivity.class);
        intent.putExtra("isEditing", true); // Indicar que estamos editando
        intent.putExtra("medicamentoId", medicamento.getId());
        intent.putExtra("nombre", medicamento.getNombre());
        intent.putExtra("dosis", medicamento.getDosis());
        intent.putExtra("horario", medicamento.getHorario());
        intent.putExtra("frecuencia", medicamento.getFrecuencia());
        intent.putExtra("diasSemana", medicamento.getDiasSemana());
        intent.putExtra("observaciones", medicamento.getObservaciones());
        context.startActivity(intent);
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
    private void setMedicamentoData(
            TextView tvNombre,
            TextView tvDosis,
            TextView tvHorario,
            TextView tvFrecuencia,
            TextView tvDiasSemana,
            TextView tvObservaciones,
            Medicamento medicamento
    ) {
        if (medicamento == null) {
            Log.e("MedicamentoPagerAdapter", "El medicamento es nulo.");
            return;
        }

        tvNombre.setText(medicamento.getNombre() != null ? medicamento.getNombre() : "Nombre no disponible");
        tvDosis.setText(medicamento.getDosis() != null ? "Dosis: " + medicamento.getDosis() : "Dosis no disponible");
        tvHorario.setText(medicamento.getHorario() != null ? "Horario: " + medicamento.getHorario() : "Horario no disponible");
        tvFrecuencia.setText(medicamento.getFrecuencia() != null ? "Frecuencia: " + medicamento.getFrecuencia() : "Frecuencia no disponible");
        tvDiasSemana.setText(medicamento.getDiasSemana() != null ? "Días: " + medicamento.getDiasSemana() : "Días no disponibles");
        tvObservaciones.setText(medicamento.getObservaciones() != null ? "Observaciones: " + medicamento.getObservaciones() : "Observaciones no disponibles");
    }

    /**
     * ViewHolder para los medicamentos.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Vistas del Medicamento 1
        TextView tvNombreMedicamento1, tvDosisMedicamento1, tvHorarioMedicamento1, tvFrecuenciaMedicamento1, tvDiasSemanaMedicamento1, tvObservaciones1;
        View linearLayoutMedicamento1;

        // Vistas del Medicamento 2
        TextView tvNombreMedicamento2, tvDosisMedicamento2, tvHorarioMedicamento2, tvFrecuenciaMedicamento2, tvDiasSemanaMedicamento2, tvObservaciones2;
        View linearLayoutMedicamento2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreMedicamento1 = itemView.findViewById(R.id.tvNombreMedicamento1);
            tvDosisMedicamento1 = itemView.findViewById(R.id.tvDosisMedicamento1);
            tvHorarioMedicamento1 = itemView.findViewById(R.id.tvHorarioMedicamento1);
            tvFrecuenciaMedicamento1 = itemView.findViewById(R.id.tvFrecuenciaMedicamento1);
            tvDiasSemanaMedicamento1 = itemView.findViewById(R.id.tvDiasSemanaMedicamento1);
            tvObservaciones1 = itemView.findViewById(R.id.tvObservaciones1);
            linearLayoutMedicamento1 = itemView.findViewById(R.id.linearLayoutMedicamento1);

            tvNombreMedicamento2 = itemView.findViewById(R.id.tvNombreMedicamento2);
            tvDosisMedicamento2 = itemView.findViewById(R.id.tvDosisMedicamento2);
            tvHorarioMedicamento2 = itemView.findViewById(R.id.tvHorarioMedicamento2);
            tvFrecuenciaMedicamento2 = itemView.findViewById(R.id.tvFrecuenciaMedicamento2);
            tvDiasSemanaMedicamento2 = itemView.findViewById(R.id.tvDiasSemanaMedicamento2);
            tvObservaciones2 = itemView.findViewById(R.id.tvObservaciones2);
            linearLayoutMedicamento2 = itemView.findViewById(R.id.linearLayoutMedicamento2);
        }
    }
}