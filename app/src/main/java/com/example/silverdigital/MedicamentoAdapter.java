package com.example.silverdigital;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.data.model.Medicamento;
import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private List<Medicamento> medicamentos;
    private OnMedicamentoClickListener listener;

    // Constructor
    public MedicamentoAdapter(List<Medicamento> medicamentos, OnMedicamentoClickListener listener) {
        this.medicamentos = medicamentos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicamentos_list, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento medicamento = medicamentos.get(position);
        holder.bind(medicamento);
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    // ViewHolder class
    public class MedicamentoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre;
        private TextView tvDosis;
        private TextView tvHorario;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDosis = itemView.findViewById(R.id.tvDosis);
            tvHorario = itemView.findViewById(R.id.tvHorario);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMedicamentoClick(medicamentos.get(position));
                }
            });
        }

        public void bind(Medicamento medicamento) {
            tvNombre.setText(medicamento.getNombre());
            tvDosis.setText("Dosis: " + medicamento.getDosis());
            tvHorario.setText("Horario: " + medicamento.getHorario());
        }
    }

    // Interface para el click listener
    public interface OnMedicamentoClickListener {
        void onMedicamentoClick(Medicamento medicamento);
    }
}