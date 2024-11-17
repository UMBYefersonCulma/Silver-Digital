package com.example.silverdigital.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.MedicamentoFormActivity; // Asegúrate de usar la actividad correcta
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
        Context context = holder.itemView.getContext(); // Contexto para lanzar actividades

        // Medicamento 1 siempre está presente
        Medicamento medicamento1 = grupo.get(0);
        holder.tvNombreMedicamento1.setText(medicamento1.getNombre() != null ? medicamento1.getNombre() : "Nombre no disponible");
        holder.tvDosisMedicamento1.setText(medicamento1.getDosis() != null ? "Dosis: " + medicamento1.getDosis() : "Dosis no disponible");
        holder.tvHorarioMedicamento1.setText(medicamento1.getHorario() != null ? "Horario: " + medicamento1.getHorario() : "Horario no disponible");

        // Listener para Medicamento 1
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MedicamentoFormActivity.class);
            intent.putExtra("medicamentoId", medicamento1.getId());
            intent.putExtra("nombre", medicamento1.getNombre());
            intent.putExtra("dosis", medicamento1.getDosis());
            intent.putExtra("horario", medicamento1.getHorario());
            context.startActivity(intent);
        });

        // Medicamento 2 (si existe)
        if (grupo.size() > 1) {
            Medicamento medicamento2 = grupo.get(1);
            holder.tvNombreMedicamento2.setText(medicamento2.getNombre() != null ? medicamento2.getNombre() : "Nombre no disponible");
            holder.tvDosisMedicamento2.setText(medicamento2.getDosis() != null ? "Dosis: " + medicamento2.getDosis() : "Dosis no disponible");
            holder.tvHorarioMedicamento2.setText(medicamento2.getHorario() != null ? "Horario: " + medicamento2.getHorario() : "Horario no disponible");
            holder.linearLayoutMedicamento2.setVisibility(View.VISIBLE);

            // Listener para Medicamento 2
            holder.linearLayoutMedicamento2.setOnClickListener(v -> {
                Intent intent = new Intent(context, MedicamentoFormActivity.class);
                intent.putExtra("medicamentoId", medicamento2.getId());
                intent.putExtra("nombre", medicamento2.getNombre());
                intent.putExtra("dosis", medicamento2.getDosis());
                intent.putExtra("horario", medicamento2.getHorario());
                context.startActivity(intent);
            });
        } else {
            holder.linearLayoutMedicamento2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return gruposMedicamentos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Vistas para el primer medicamento
        TextView tvNombreMedicamento1, tvDosisMedicamento1, tvHorarioMedicamento1;

        // Vistas para el segundo medicamento
        TextView tvNombreMedicamento2, tvDosisMedicamento2, tvHorarioMedicamento2;
        View linearLayoutMedicamento2; // Contenedor para el segundo medicamento

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