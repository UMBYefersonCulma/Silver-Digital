package com.example.silverdigital.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silverdigital.R;

import java.util.List;

public class ConsejosAdapter extends RecyclerView.Adapter<ConsejosAdapter.ConsejosViewHolder> {

    private final List<String> consejosList;
    private final List<String> urlList; // Lista de URLs

    public ConsejosAdapter(List<String> consejosList, List<String> urlList) {
        this.consejosList = consejosList;
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public ConsejosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consejo, parent, false);
        return new ConsejosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsejosViewHolder holder, int position) {
        String consejo = consejosList.get(position);
        String url = urlList.get(position); // Obtén la URL correspondiente

        holder.tvConsejo.setText(consejo);

        // Configurar el clic en el botón
        holder.btnEnlace.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return consejosList.size();
    }

    public static class ConsejosViewHolder extends RecyclerView.ViewHolder {
        TextView tvConsejo;
        ImageButton btnEnlace;

        public ConsejosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvConsejo = itemView.findViewById(R.id.tvConsejo);
            btnEnlace = itemView.findViewById(R.id.btnEnlace);
        }
    }
}