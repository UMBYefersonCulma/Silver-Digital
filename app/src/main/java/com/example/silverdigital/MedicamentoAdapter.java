package com.example.silverdigital;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.data.model.Medicamento;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private List<Medicamento> medicamentos = new ArrayList<>();
    private final OnMedicamentoClickListener listener;

    // Constructor
    public MedicamentoAdapter(OnMedicamentoClickListener listener) {
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

    // Método para actualizar los datos del adaptador
    public void updateData(List<Medicamento> nuevosMedicamentos) {
        medicamentos.clear();
        medicamentos.addAll(nuevosMedicamentos);
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    // ViewHolder class
    public class MedicamentoViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNombre;
        private final TextView tvDosis;
        private final TextView tvHorario;
        private final TextView tvObservaciones; // Añadido para mostrar observaciones

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDosis = itemView.findViewById(R.id.tvDosis);
            tvHorario = itemView.findViewById(R.id.tvHorario);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones); // Asociar el nuevo campo

            // Configurar el click listener para cada ítem
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMedicamentoClick(medicamentos.get(position));
                }
            });
        }

        public void bind(Medicamento medicamento) {
            tvNombre.setText(medicamento.getNombre() != null ? medicamento.getNombre() : "Nombre no disponible");
            tvDosis.setText(medicamento.getDosis() != null ? "Dosis: " + medicamento.getDosis() : "Dosis no disponible");
            tvHorario.setText(medicamento.getHorario() != null ? "Horario: " + medicamento.getHorario() : "Horario no disponible");
            tvObservaciones.setText(medicamento.getObservaciones() != null ? "Observaciones: "+ medicamento.getObservaciones(): "Observaciones no disponibles");
        }
    }

    // Interface para el click listener
    public interface OnMedicamentoClickListener {
        void onMedicamentoClick(Medicamento medicamento);
    }
}