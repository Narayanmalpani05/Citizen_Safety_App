package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.example.safeu2.models.LowerCaseGenerator;
import com.example.safeu2.models.NumericGenerator;
import com.example.safeu2.models.PasswordGenerator;
import com.example.safeu2.models.SpecialCharGenerator;
import com.example.safeu2.models.UpperCaseGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class strongPassGen extends AppCompatActivity {
    private TextInputEditText editPasswordSize;
    private TextView textPasswordGenerated, textErrorMessage;
    private MaterialCheckBox checkLower, checkUpper, checkSpecialChar, checkNumeric;
    private MaterialButton btnGenerate, btnCopy, btn_gotopassman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strong_pass_gen);

        initViews();
        clickListeners();
    }

    private void clickListeners() {
        btnGenerate.setOnClickListener(view -> {
            int passwordSize;
            try {
                passwordSize = Integer.parseInt(editPasswordSize.getText().toString());
            } catch (NumberFormatException e) {
                textErrorMessage.setText("Invalid password size");
                return;
            }

            textErrorMessage.setText("");

            if(passwordSize < 8){
                textErrorMessage.setText("Password Size must be at least 8");
                return;
            }

            PasswordGenerator.clear();
            if(checkLower.isChecked()) PasswordGenerator.add(new LowerCaseGenerator());
            if(checkNumeric.isChecked()) PasswordGenerator.add(new NumericGenerator());
            if(checkUpper.isChecked()) PasswordGenerator.add(new UpperCaseGenerator());
            if(checkSpecialChar.isChecked()) PasswordGenerator.add(new SpecialCharGenerator());


            if(PasswordGenerator.isEmpty()){
                textErrorMessage.setText("Please select at least one password content type");
                return;
            }

            String passwrd = PasswordGenerator.generatePassword(passwordSize);
            textPasswordGenerated.setText(passwrd);

        });

        btnCopy.setOnClickListener(view -> {
            String pass = textPasswordGenerated.getText().toString();
            if(pass.equals("Your new password") || pass.isEmpty()) {
                Toast.makeText(this, "Generate a password first", Toast.LENGTH_SHORT).show();
                return;
            }
            ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("password", pass));
            Toast.makeText(this, "Password Copied", Toast.LENGTH_SHORT).show();
        });

        btn_gotopassman.setOnClickListener(view -> {
            String pass = textPasswordGenerated.getText().toString();
            if(!pass.equals("Your new password") && !pass.isEmpty()) {
                ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText("password", pass));
                Toast.makeText(this, "Password Copied", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(strongPassGen.this, EditorActivity.class);
            startActivity(intent);
        });

    }

    private void initViews() {
        editPasswordSize = findViewById(R.id.edit_pwd_size);
        textPasswordGenerated = findViewById(R.id.text_password_result);
        textErrorMessage = findViewById(R.id.text_error);
        checkLower = findViewById(R.id.check_lower);
        checkUpper = findViewById(R.id.check_upper);
        checkSpecialChar = findViewById(R.id.check_special_char);
        checkNumeric = findViewById(R.id.check_numeric);
        btnGenerate = findViewById(R.id.btn_generate);
        btnCopy = findViewById(R.id.btn_copy);
        btn_gotopassman = findViewById(R.id.btn_gotopassman);
    }
}