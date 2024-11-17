package com.example.silverdigital.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.R;
import com.example.silverdigital.data.model.Medicamento;
import java.util.List;

public class MedicamentoAdapterAgrupado extends RecyclerView.Adapter<MedicamentoAdapterAgrupado.ViewHolder> {

    private final List<List<Medicamento>> grupos;

    public MedicamentoAdapterAgrupado(List<List<Medicamento>> grupos) {
        this.grupos = grupos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicamento_grupo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Medicamento> grupo = grupos.get(position);

        // Medicamento 1
        if (!grupo.isEmpty()) {
            Medicamento medicamento1 = grupo.get(0);
            holder.tvNombreMedicamento1.setText(medicamento1.getNombre());
            holder.tvDosisMedicamento1.setText("Dosis: " + medicamento1.getDosis());
            holder.tvHorarioMedicamento1.setText("Horario: " + medicamento1.getHorario());
        }

        // Medicamento 2 (si existe)
        if (grupo.size() > 1) {
            Medicamento medicamento2 = grupo.get(1);
            holder.tvNombreMedicamento2.setText(medicamento2.getNombre());
            holder.tvDosisMedicamento2.setText("Dosis: " + medicamento2.getDosis());
            holder.tvHorarioMedicamento2.setText("Horario: " + medicamento2.getHorario());
        } else {
            holder.linearLayoutMedicamento2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return grupos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreMedicamento1, tvDosisMedicamento1, tvHorarioMedicamento1;
        TextView tvNombreMedicamento2, tvDosisMedicamento2, tvHorarioMedicamento2;
        LinearLayout linearLayoutMedicamento2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreMedicamento1 = itemView.findViewById(R.id.tvNombreMedicamento1);
            tvDosisMedicamento1 = itemView.findViewById(R.id.tvDosisMedicamento1);
            tvHorarioMedicamento1 = itemView.findViewById(R.id.tvHorarioMedicamento1);
            tvNombreMedicamento2 = itemView.findViewById(R.id.tvNombreMedicamento2);
            tvDosisMedicamento2 = itemView.findViewById(R.id.tvDosisMedicamento2);
            tvHorarioMedicamento2 = itemView.findViewById(R.id.tvHorarioMedicamento2);
            linearLayoutMedicamento2 = itemView.findViewById(R.id.linearLayoutMedicamento2);
        }
    }
}