package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.safeu2.database.VaultEntry;
import com.example.safeu2.database.VaultRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditorActivity extends AppCompatActivity {

    private TextInputEditText mNameEditText;
    private TextInputEditText mEmailOrIdEditText;
    private TextInputEditText mPasswordEditText;
    private Spinner mSpinner;
    private MaterialButton mSaveButton;

    private VaultRepository vaultRepository;
    private String currentEntryId = null;
    private VaultEntry currentEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = findViewById(R.id.nameEditText);
        mEmailOrIdEditText = findViewById(R.id.emailOrIdEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mSpinner = findViewById(R.id.spinner);
        mSaveButton = findViewById(R.id.saveButton);

        vaultRepository = new VaultRepository(this);

        setUpSpinner();

        Intent intent = getIntent();
        if (intent.hasExtra("ENTRY_ID")) {
            currentEntryId = intent.getStringExtra("ENTRY_ID");
            currentEntry = vaultRepository.getEntryById(currentEntryId);
        }

        if (currentEntry == null) {
            setTitle("Add Account");
        } else {
            setTitle("Edit Account");
            mNameEditText.setText(currentEntry.getAccountName());
            mEmailOrIdEditText.setText(currentEntry.getEmailOrId());
            mPasswordEditText.setText(currentEntry.getPassword());
            
            if ("Work".equals(currentEntry.getType())) {
                mSpinner.setSelection(1);
            } else if ("Other".equals(currentEntry.getType())) {
                mSpinner.setSelection(2);
            } else {
                mSpinner.setSelection(0); // Personal
            }
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    private void setUpSpinner() {
        String[] types = new String[]{"Personal", "Work", "Other"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(spinnerAdapter);
    }

    private void saveContact() {
        String name = mNameEditText.getText().toString().trim();
        String emailOrId = mEmailOrIdEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String type = mSpinner.getSelectedItem().toString();

        if (name.isEmpty() || emailOrId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentEntry == null) {
            VaultEntry newEntry = new VaultEntry(name, emailOrId, password, type);
            vaultRepository.addEntry(newEntry);
            Toast.makeText(this, "Account Saved", Toast.LENGTH_SHORT).show();
        } else {
            currentEntry.setAccountName(name);
            currentEntry.setEmailOrId(emailOrId);
            currentEntry.setPassword(password);
            currentEntry.setType(type);
            vaultRepository.updateEntry(currentEntry);
            Toast.makeText(this, "Account Updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}