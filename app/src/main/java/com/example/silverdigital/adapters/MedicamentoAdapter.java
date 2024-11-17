package com.example.silverdigital.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.R;
import com.example.silverdigital.data.model.Medicamento;

import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private final List<Medicamento> medicamentos;
    private final OnMedicamentoClickListener listener;

    public MedicamentoAdapter(List<Medicamento> medicamentos, OnMedicamentoClickListener listener) {
        this.medicamentos = medicamentos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        // Verificar que la posición sea válida y que el objeto no sea nulo
        if (position >= 0 && position < getItemCount()) {
            Medicamento medicamento = medicamentos.get(position);
            if (medicamento != null) {
                holder.tvNombre.setText(medicamento.getNombre() != null ? medicamento.getNombre() : "Sin nombre");
                holder.tvDosis.setText(medicamento.getDosis() != null ? "Dosis: " + medicamento.getDosis() : "Dosis no especificada");
                holder.tvHorario.setText(medicamento.getHorario() != null ? "Horario: " + medicamento.getHorario() : "Horario no especificado");
                // Configurar el click listener solo si el medicamento es válido
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onMedicamentoClick(medicamento);
                    }
                });
            } else {
                Log.e("MedicamentoAdapter", "Medicamento en posición " + position + " es nulo");
                setDefaultText(holder);
            }
        } else {
            Log.e("MedicamentoAdapter", "Posición inválida: " + position);
            setDefaultText(holder);
        }
    }

    @Override
    public int getItemCount() {
        // Asegúrate de que la lista no sea nula antes de devolver su tamaño
        return medicamentos != null ? medicamentos.size() : 0;
    }

    // Asignar texto por defecto en caso de errores
    private void setDefaultText(@NonNull MedicamentoViewHolder holder) {
        holder.tvNombre.setText("Medicamento desconocido");
        holder.tvDosis.setText("Dosis no disponible");
        holder.tvHorario.setText("Horario no disponible");
    }

    public interface OnMedicamentoClickListener {
        void onMedicamentoClick(Medicamento medicamento);
    }

    static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDosis, tvHorario;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Verifica y enlaza las vistas desde el layout
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDosis = itemView.findViewById(R.id.tvDosis);
            tvHorario = itemView.findViewById(R.id.tvHorario);

            // Validación en caso de que alguna vista no esté enlazada correctamente
            if (tvNombre == null || tvDosis == null || tvHorario == null) {
                Log.e("MedicamentoAdapter", "Una o más vistas no fueron encontradas en el layout");
            }
        }
    }
}