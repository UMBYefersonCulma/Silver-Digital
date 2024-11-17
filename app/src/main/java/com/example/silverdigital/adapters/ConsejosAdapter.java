package com.example.silverdigital.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.silverdigital.R;

public class ConsejosAdapter extends RecyclerView.Adapter<ConsejosAdapter.ConsejosViewHolder> {

    private final List<String> consejos;

    public ConsejosAdapter(List<String> consejos) {
        this.consejos = consejos;
    }

    @NonNull
    @Override
    public ConsejosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consejo, parent, false);
        return new ConsejosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsejosViewHolder holder, int position) {
        holder.tvConsejo.setText(consejos.get(position));
    }

    @Override
    public int getItemCount() {
        return consejos.size();
    }

    static class ConsejosViewHolder extends RecyclerView.ViewHolder {
        TextView tvConsejo;

        public ConsejosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvConsejo = itemView.findViewById(R.id.tvConsejo);
        }
    }
}