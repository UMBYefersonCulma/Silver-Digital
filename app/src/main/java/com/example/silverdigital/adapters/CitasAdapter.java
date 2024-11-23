package com.example.silverdigital.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.silverdigital.R;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitasViewHolder> {

    private final List<String> citas;

    public CitasAdapter(List<String> citas) {
        this.citas = citas;
    }

    @NonNull
    @Override
    public CitasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasViewHolder holder, int position) {
        holder.tvCita.setText(citas.get(position));
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class CitasViewHolder extends RecyclerView.ViewHolder {
        TextView tvCita;

        public CitasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCita = itemView.findViewById(R.id.tvCita);
        }
    }
}