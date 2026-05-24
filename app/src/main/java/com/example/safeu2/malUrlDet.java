package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safeu2.api.PredictionResponse;
import com.example.safeu2.viewmodel.MalUrlViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class malUrlDet extends AppCompatActivity {

    private TextInputEditText editText;
    private Button button;
    private MaterialCardView resultCard;
    private ProgressBar progressBar;
    private ImageView statusIcon;
    private TextView textView;
    private TextView confidenceText;
    private TextView errorText;

    private MalUrlViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mal_url_det);

        // Initialize Views
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        resultCard = findViewById(R.id.resultCard);
        progressBar = findViewById(R.id.progressBar);
        statusIcon = findViewById(R.id.statusIcon);
        textView = findViewById(R.id.textView);
        confidenceText = findViewById(R.id.confidenceText);
        errorText = findViewById(R.id.errorText);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MalUrlViewModel.class);

        // Setup Observers
        setupObservers();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = editText.getText().toString();
                viewModel.predictUrl(url);
            }
        });
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                resultCard.setVisibility(View.VISIBLE);
                if (isLoading) {
                    progressBar.setVisibility(View.VISIBLE);
                    statusIcon.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    confidenceText.setVisibility(View.GONE);
                    errorText.setVisibility(View.GONE);
                    button.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                }
            }
        });

        viewModel.getStatusMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                if (msg != null && !msg.isEmpty()) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(msg);
                    textView.setTextColor(Color.DKGRAY);
                }
            }
        });

        viewModel.getPrediction().observe(this, new Observer<PredictionResponse>() {
            @Override
            public void onChanged(PredictionResponse response) {
                if (response == null) return;
                
                textView.setVisibility(View.VISIBLE);
                confidenceText.setVisibility(View.VISIBLE);
                statusIcon.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.GONE);

                String output = response.getOutput();
                double confidence = response.getConfidence() * 100;
                
                confidenceText.setText(String.format("Confidence: %.2f%%", confidence));

                if ("bad".equalsIgnoreCase(output) || "0".equals(output)) {
                    textView.setText("MALICIOUS URL DETECTED");
                    textView.setTextColor(getResources().getColor(R.color.error));
                    statusIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                    statusIcon.setColorFilter(getResources().getColor(R.color.error));
                } else {
                    textView.setText("SAFE URL");
                    textView.setTextColor(getResources().getColor(R.color.success));
                    statusIcon.setImageResource(android.R.drawable.ic_dialog_info);
                    statusIcon.setColorFilter(getResources().getColor(R.color.success));
                }
            }
        });

        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMsg) {
                if (errorMsg == null) return;
                
                errorText.setVisibility(View.VISIBLE);
                errorText.setText(errorMsg);
                
                textView.setVisibility(View.GONE);
                confidenceText.setVisibility(View.GONE);
                statusIcon.setVisibility(View.GONE);
            }
        });
    }
}
