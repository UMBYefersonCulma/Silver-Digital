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

public class MedicamentoHorizontalAdapter extends RecyclerView.Adapter<MedicamentoHorizontalAdapter.ViewHolder> {

    private final List<Medicamento> medicamentos;

    public MedicamentoHorizontalAdapter(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicamento medicamento = medicamentos.get(position);
        holder.tvNombre.setText(medicamento.getNombre());
        holder.tvDosis.setText("Dosis: " + medicamento.getDosis());
        holder.tvHorario.setText("Horario: " + medicamento.getHorario());
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDosis, tvHorario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDosis = itemView.findViewById(R.id.tvDosis);
            tvHorario = itemView.findViewById(R.id.tvHorario);
        }
    }
}