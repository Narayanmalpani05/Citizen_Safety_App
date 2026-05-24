package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safeu2.database.VaultRepository;
import com.google.android.material.textfield.TextInputEditText;

public class VaultPinActivity extends AppCompatActivity {

    private TextInputEditText pinEditText;
    private Button submitPinButton;
    private TextView titleTextView;
    private TextView descTextView;
    private VaultRepository vaultRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_pin);

        pinEditText = findViewById(R.id.pinEditText);
        submitPinButton = findViewById(R.id.submitPinButton);
        titleTextView = findViewById(R.id.titleTextView);
        descTextView = findViewById(R.id.descTextView);

        vaultRepository = new VaultRepository(this);

        if (!vaultRepository.hasPin()) {
            titleTextView.setText("Setup Vault PIN");
            descTextView.setText("Create a new PIN to secure your passwords");
            submitPinButton.setText("CREATE PIN");
        }

        submitPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEditText.getText().toString();
                if (pin.length() < 4) {
                    Toast.makeText(VaultPinActivity.this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!vaultRepository.hasPin()) {
                    vaultRepository.setPin(pin);
                    Toast.makeText(VaultPinActivity.this, "PIN Created", Toast.LENGTH_SHORT).show();
                    openVault();
                } else {
                    if (vaultRepository.verifyPin(pin)) {
                        openVault();
                    } else {
                        Toast.makeText(VaultPinActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                        pinEditText.setText("");
                    }
                }
            }
        });
    }

    private void openVault() {
        Intent intent = new Intent(VaultPinActivity.this, PasswordManager.class);
        startActivity(intent);
        finish(); // Close pin activity so user can't press back to it
    }
}
