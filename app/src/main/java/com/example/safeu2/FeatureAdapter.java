package com.example.safeu2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {

    private String[] titles;
    private int[] icons;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public FeatureAdapter(String[] titles, int[] icons, OnItemClickListener listener) {
        this.titles = titles;
        this.icons = icons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        holder.title.setText(titles[position]);
        holder.icon.setImageResource(icons[position]);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public static class FeatureViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.featureTitle);
            icon = itemView.findViewById(R.id.featureIcon);
        }
    }
}
