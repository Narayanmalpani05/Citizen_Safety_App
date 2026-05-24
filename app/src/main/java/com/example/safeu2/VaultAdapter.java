package com.example.safeu2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safeu2.database.VaultEntry;

import java.util.List;

public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.VaultViewHolder> {

    private List<VaultEntry> entries;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(VaultEntry entry);
        void onDeleteClick(VaultEntry entry);
    }

    public VaultAdapter(List<VaultEntry> entries, OnItemClickListener listener) {
        this.entries = entries;
        this.listener = listener;
    }

    public void setEntries(List<VaultEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vault_entry, parent, false);
        return new VaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaultViewHolder holder, int position) {
        VaultEntry entry = entries.get(position);
        holder.accountName.setText(entry.getAccountName());
        holder.emailOrId.setText(entry.getEmailOrId());
        
        holder.itemView.setOnClickListener(v -> listener.onItemClick(entry));
        holder.deleteIcon.setOnClickListener(v -> listener.onDeleteClick(entry));
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    public static class VaultViewHolder extends RecyclerView.ViewHolder {
        TextView accountName;
        TextView emailOrId;
        ImageView deleteIcon;

        public VaultViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            emailOrId = itemView.findViewById(R.id.emailOrId);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
