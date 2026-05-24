package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.safeu2.database.VaultEntry;
import com.example.safeu2.database.VaultRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PasswordManager extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VaultAdapter adapter;
    private VaultRepository vaultRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);

        vaultRepository = new VaultRepository(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordManager.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new VaultAdapter(vaultRepository.getEntries(), new VaultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VaultEntry entry) {
                Intent intent = new Intent(PasswordManager.this, EditorActivity.class);
                intent.putExtra("ENTRY_ID", entry.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(VaultEntry entry) {
                new AlertDialog.Builder(PasswordManager.this)
                        .setTitle("Delete Password")
                        .setMessage("Are you sure you want to delete the password for " + entry.getAccountName() + "?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                vaultRepository.deleteEntry(entry.getId());
                                loadEntries();
                                Toast.makeText(PasswordManager.this, "Password deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEntries();
    }

    private void loadEntries() {
        List<VaultEntry> entries = vaultRepository.getEntries();
        adapter.setEntries(entries);
    }
}