package com.example.silverdigital.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silverdigital.R;
import com.example.silverdigital.data.model.Appointment;

import java.util.List;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.ViewHolder> {

    private final List<Appointment> citasList;
    private final OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onAppointmentClicked(Appointment cita);
    }

    public CitasAdapter(List<Appointment> citasList, OnAppointmentClickListener listener) {
        this.citasList = citasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment cita = citasList.get(position);

        // Valida los valores antes de asignarlos
        String doctorName = cita.getDoctorName() != null ? cita.getDoctorName() : "Sin Nombre";
        String date = cita.getDate() != null ? cita.getDate() : "Sin Fecha";

        // Log de depuración
        Log.d("CitasAdapter", "Cargando cita: ID=" + cita.getId() + ", Doctor=" + doctorName + ", Fecha=" + date);

        // Configurar los valores en las vistas
        holder.tvDoctorName.setText(doctorName);
        holder.tvDate.setText(date);

        // Listener para clics
        holder.itemView.setOnClickListener(v -> {
            Log.d("CitasAdapter", "Clic en cita: ID=" + cita.getId());
            listener.onAppointmentClicked(cita);
        });
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}