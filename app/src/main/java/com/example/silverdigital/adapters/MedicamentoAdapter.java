package com.example.silverdigital.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.R;
import com.example.silverdigital.data.model.Medicamento;
import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder> {

    private List<List<Medicamento>> gruposMedicamentos;

    public MedicamentoAdapter(List<List<Medicamento>> gruposMedicamentos) {
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

        // Configurar Medicamento 1
        Medicamento medicamento1 = grupo.get(0);
        setMedicamentoData(holder.tvNombreMedicamento1, holder.tvDosisMedicamento1, holder.tvHorarioMedicamento1, medicamento1);

        // Configurar Medicamento 2 (si existe)
        if (grupo.size() > 1) {
            Medicamento medicamento2 = grupo.get(1);
            setMedicamentoData(holder.tvNombreMedicamento2, holder.tvDosisMedicamento2, holder.tvHorarioMedicamento2, medicamento2);
            holder.linearLayoutMedicamento2.setVisibility(View.VISIBLE);
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
    public void updateData(List<List<Medicamento>> nuevosGrupos) {
        this.gruposMedicamentos.clear(); // Limpiar los datos actuales
        this.gruposMedicamentos.addAll(nuevosGrupos); // Agregar los nuevos grupos
        notifyDataSetChanged(); // Notificar cambios al adaptador
    }

    /**
     * Configurar los datos del medicamento en las vistas
     */
    private void setMedicamentoData(TextView tvNombre, TextView tvDosis, TextView tvHorario, Medicamento medicamento) {
        tvNombre.setText(medicamento.getNombre() != null ? medicamento.getNombre() : "Nombre no disponible");
        tvDosis.setText(medicamento.getDosis() != null ? "Dosis: " + medicamento.getDosis() : "Dosis no disponible");
        tvHorario.setText(medicamento.getHorario() != null ? "Horario: " + medicamento.getHorario() : "Horario no disponible");
    }

    /**
     * ViewHolder para los medicamentos
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreMedicamento1, tvDosisMedicamento1, tvHorarioMedicamento1;
        TextView tvNombreMedicamento2, tvDosisMedicamento2, tvHorarioMedicamento2;
        View linearLayoutMedicamento2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asociar vistas del primer medicamento
            tvNombreMedicamento1 = itemView.findViewById(R.id.tvNombreMedicamento1);
            tvDosisMedicamento1 = itemView.findViewById(R.id.tvDosisMedicamento1);
            tvHorarioMedicamento1 = itemView.findViewById(R.id.tvHorarioMedicamento1);

            // Asociar vistas del segundo medicamento
            tvNombreMedicamento2 = itemView.findViewById(R.id.tvNombreMedicamento2);
            tvDosisMedicamento2 = itemView.findViewById(R.id.tvDosisMedicamento2);
            tvHorarioMedicamento2 = itemView.findViewById(R.id.tvHorarioMedicamento2);
            linearLayoutMedicamento2 = itemView.findViewById(R.id.linearLayoutMedicamento2);
        }
    }
}